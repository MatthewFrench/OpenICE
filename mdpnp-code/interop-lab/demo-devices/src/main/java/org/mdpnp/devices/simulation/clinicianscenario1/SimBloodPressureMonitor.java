/*******************************************************************************
 * Copyright (c) 2014, MD PnP Program
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package org.mdpnp.devices.simulation.clinicianscenario1;

import ice.Numeric;

import org.mdpnp.devices.simulation.AbstractSimulatedConnectedDevice;
import org.mdpnp.rtiapi.data.EventLoop;
import org.mdpnp.rtiapi.data.QosProfiles;
import org.mdpnp.rtiapi.data.EventLoop.ConditionHandler;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.infrastructure.Condition;
import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.infrastructure.RETCODE_NO_DATA;
import com.rti.dds.infrastructure.ResourceLimitsQosPolicy;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.subscription.InstanceStateKind;
import com.rti.dds.subscription.ReadCondition;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.SampleInfoSeq;
import com.rti.dds.subscription.SampleStateKind;
import com.rti.dds.subscription.ViewStateKind;
import com.rti.dds.topic.Topic;

/**
 * @author Jeff Plourde
 *
 */
public class SimBloodPressureMonitor extends AbstractSimulatedConnectedDevice {

    protected InstanceHolder<Numeric> systolic, diastolic, pulse, inflation, nextInflationTime, state;
    // TODO needs to subscribe to an objective state for triggering a NIBP
    HeartRateIncreaseDataReader heartRateIncreaseDataReader;
    Topic heartRateIncreaseTopic;
    ReadCondition heartRateObjectiveCondition;

    private final SimulatedBloodPressureMonitor bloodPressure = new SimulatedBloodPressureMonitor() {
        @Override
        protected void beginDeflation() {
            numericSample(state, ice.MDC_EVT_STAT_NBP_DEFL_AND_MEAS_BP.VALUE, null);
        }

        @Override
        protected void beginInflation() {
            numericSample(state, ice.MDC_EVT_STAT_NBP_INFL_TO_MAX_CUFF_PRESS.VALUE, null);
        }

        @Override
        protected void endDeflation() {
            numericSample(state, ice.MDC_EVT_STAT_OFF.VALUE, null);
        }

        @Override
        protected void updateInflation(int inflation) {
            numericSample(SimBloodPressureMonitor.this.inflation, inflation, null);
        }

        @Override
        protected void updateNextInflationTime(long nextInflationTime) {
            numericSample(SimBloodPressureMonitor.this.nextInflationTime, nextInflationTime, null);
        }

        @Override
        protected void updateReading(int systolic, int diastolic, int pulse) {
        	
            numericSample(SimBloodPressureMonitor.this.systolic, systolic, null);
            numericSample(SimBloodPressureMonitor.this.diastolic, diastolic, null);
            numericSample(SimBloodPressureMonitor.this.pulse, pulse, null);
        }
    };

    public SimBloodPressureMonitor(int domainId, EventLoop eventLoop) {
        super(domainId, eventLoop);
        deviceIdentity.model = "Clinician Scenario 1 Blood Pressure (Simulated)";
        writeDeviceIdentity();

        state = createNumericInstance(rosetta.MDC_PRESS_CUFF.VALUE);
        systolic = createNumericInstance(rosetta.MDC_PRESS_CUFF_SYS.VALUE);
        diastolic = createNumericInstance(rosetta.MDC_PRESS_CUFF_DIA.VALUE);
        nextInflationTime = createNumericInstance(ice.MDC_PRESS_CUFF_NEXT_INFLATION.VALUE);
        inflation = createNumericInstance(ice.MDC_PRESS_CUFF_INFLATION.VALUE);
        // TODO temporarily more interesting
        pulse = createNumericInstance(rosetta.MDC_PULS_RATE_NON_INV.VALUE);
        // pulse =
        // createNumericInstance(ice.Physio.MDC_PULS_RATE_NON_INV.value());

        numericSample(state, ice.MDC_EVT_STAT_OFF.VALUE, null);
        
        HeartRateIncreaseObjectiveTypeSupport.register_type(domainParticipant, HeartRateIncreaseObjectiveTypeSupport.get_type_name());
        heartRateIncreaseTopic = domainParticipant.create_topic(HeartRateIncreaseObjectiveTopic.VALUE,
        		HeartRateIncreaseObjectiveTypeSupport.get_type_name(), DomainParticipant.TOPIC_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        
        heartRateIncreaseDataReader = (HeartRateIncreaseDataReader) subscriber.create_datareader_with_profile(
        		heartRateIncreaseTopic, QosProfiles.ice_library, QosProfiles.state, null, StatusKind.STATUS_MASK_NONE);
        
        final SampleInfoSeq info_seq = new SampleInfoSeq();
        final HeartRateIncreaseObjectiveSeq data_seq = new HeartRateIncreaseObjectiveSeq();
        
        eventLoop.addHandler(
                heartRateObjectiveCondition = heartRateIncreaseDataReader.create_readcondition(SampleStateKind.NOT_READ_SAMPLE_STATE,
                        ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ANY_INSTANCE_STATE), new ConditionHandler() {
                    @Override
                    public void conditionChanged(Condition condition) {
                        try {
                        	heartRateIncreaseDataReader.read_w_condition(data_seq, info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED,
                                    (ReadCondition) condition);
                            for (int i = 0; i < data_seq.size(); i++) {
                                SampleInfo si = (SampleInfo) info_seq.get(i);
                                HeartRateIncreaseObjective obj = (HeartRateIncreaseObjective) data_seq.get(i);
                                
                                System.out.println("Got heart rate increase objective");
                                bloodPressure.setDecreasedInterval();

                                if (0 != (si.view_state & ViewStateKind.NEW_VIEW_STATE) && si.valid_data) {
                                    //log.warn("Handle for metric_id=" + obj.metric_id + " is " + si.instance_handle);
                                    //instanceMetrics.put(new InstanceHandle_t(si.instance_handle), obj.metric_id);
                                }

                                if (0 != (si.instance_state & InstanceStateKind.ALIVE_INSTANCE_STATE)) {
                                    if (si.valid_data) {
                                        //log.warn("Setting " + obj.metric_id + " to [ " + obj.lower + " , " + obj.upper + "]");
                                        //setAlarmSettings(obj);
                                    }
                                } else {
                                    obj = new HeartRateIncreaseObjective();
                                    //log.warn("Unsetting handle " + si.instance_handle);
                                    // TODO 1-Oct-2013 JP This call to
                                    // get_key_value fails consistently on
                                    // ARM platforms
                                    // so I'm tracking instances externally
                                    // for the time being
                                    // alarmSettingsObjectiveReader.get_key_value(obj,
                                    // si.instance_handle);
                                    //String metricId = instanceMetrics.get(si.instance_handle);
                                    //log.warn("Unsetting " + metricId);
                                    //if (null != metricId) {
                                        //unsetAlarmSettings(metricId);
                                    //}

                                }
                            }
                        } catch (RETCODE_NO_DATA noData) {

                        } finally {
                        	heartRateIncreaseDataReader.return_loan(data_seq, info_seq);
                        }
                    }
                });
    }

    @Override
    public boolean connect(String str) {
        bloodPressure.connect(str);
        return super.connect(str);
    }

    @Override
    public void disconnect() {
        bloodPressure.disconnect();
        super.disconnect();
    }

    @Override
    public void shutdown() {
        disconnect();
        super.shutdown();
    }

    @Override
    protected String iconResourceName() {
        return "nbp.png";
    }
}
