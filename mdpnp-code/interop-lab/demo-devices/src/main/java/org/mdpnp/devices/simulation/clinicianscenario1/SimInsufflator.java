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

import ice.GlobalSimulationObjective;

import org.mdpnp.devices.simulation.AbstractSimulatedConnectedDevice;
import org.mdpnp.rtiapi.data.EventLoop;
import org.mdpnp.rtiapi.data.QosProfiles;
import org.mdpnp.rtiapi.data.EventLoop.ConditionHandler;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.infrastructure.Condition;
import com.rti.dds.infrastructure.RETCODE_NO_DATA;
import com.rti.dds.infrastructure.ResourceLimitsQosPolicy;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.infrastructure.Time_t;
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
public class SimInsufflator extends AbstractSimulatedConnectedDevice {

    protected final InstanceHolder<ice.Numeric> insufflatorPressure;
    protected final InstanceHolder<ice.Numeric> insufflatorStatus;

    private final Time_t sampleTime = new Time_t(0, 0);
    
    InsufflatorStateDataReader insufflatorStateDataReader;
    Topic insufflatorStateTopic;
    ReadCondition insufflatorStateCondition;

    private class MySimClinicianScenario1Insufflator extends SimulatedInsufflator {
        @Override
        protected void receiveInsufflator(long timestamp, boolean onStatus, int pressure) {
            sampleTime.sec = (int) (timestamp / 1000L);
            sampleTime.nanosec = (int) (timestamp % 1000L * 1000000L);
            numericSample(insufflatorPressure, pressure, sampleTime);
            numericSample(insufflatorStatus, (onStatus) ? 1 : 0, sampleTime);
        }
    }

    private final MySimClinicianScenario1Insufflator insufflator = new MySimClinicianScenario1Insufflator();

    @Override
    public boolean connect(String str) {
    	insufflator.connect(executor);
        return super.connect(str);
    }

    @Override
    public void disconnect() {
    	insufflator.disconnect();
        super.disconnect();
    }

    public SimInsufflator(int domainId, EventLoop eventLoop) {
        super(domainId, eventLoop);

        insufflatorPressure = createNumericInstance(rosetta.CLINICIAN_SCENARIO_1_INSUFFLATOR_PRESSURE.VALUE);
        insufflatorStatus = createNumericInstance(rosetta.CLINICIAN_SCENARIO_1_INSUFFLATOR_STATUS.VALUE);

        deviceIdentity.model = "Clinician Scenario 1 Insufflator (Simulated)";
        writeDeviceIdentity();
        
        createInsufflatorStateReader(eventLoop);
    }

    @Override
    protected String iconResourceName() {
        return "pulseox.png"; //Cause ynaut
    }

    @Override
    public void simulatedNumeric(GlobalSimulationObjective obj) {
        // Currently the super ctor registers for this callback; so pulseox might not yet be initialized
        //if (obj != null && insufflator != null) {
        //    if (rosetta.MDC_PULS_OXIM_PULS_RATE.VALUE.equals(obj.metric_id)) {
        //    	insufflator.setTargetinsufflatorState((double) obj.value);
        //    } else if (rosetta.MDC_PULS_OXIM_SAT_O2.VALUE.equals(obj.metric_id)) {
        //    	insufflator.setTargetSpO2((double) obj.value);
        //    }
        //}
    }
    
    @Override
    protected boolean sampleArraySpecifySourceTimestamp() {
        return true;
    }
    
    void createInsufflatorStateReader(EventLoop eventLoop) {
    	 InsufflatorStateObjectiveTypeSupport.register_type(domainParticipant, InsufflatorStateObjectiveTypeSupport.get_type_name());
         insufflatorStateTopic = domainParticipant.create_topic(InsufflatorStateObjectiveTopic.VALUE,
         		InsufflatorStateObjectiveTypeSupport.get_type_name(), DomainParticipant.TOPIC_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
         
         insufflatorStateDataReader = (InsufflatorStateDataReader) subscriber.create_datareader_with_profile(
         		insufflatorStateTopic, QosProfiles.ice_library, QosProfiles.state, null, StatusKind.STATUS_MASK_NONE);
         
         final SampleInfoSeq info_seq = new SampleInfoSeq();
         final InsufflatorStateObjectiveSeq data_seq = new InsufflatorStateObjectiveSeq();
         
         eventLoop.addHandler(
        		 insufflatorStateCondition = insufflatorStateDataReader.create_readcondition(SampleStateKind.NOT_READ_SAMPLE_STATE,
                         ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ANY_INSTANCE_STATE), new ConditionHandler() {
                     @Override
                     public void conditionChanged(Condition condition) {
                         try {
                         	insufflatorStateDataReader.read_w_condition(data_seq, info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED,
                                     (ReadCondition) condition);
                             for (int i = 0; i < data_seq.size(); i++) {
                                 SampleInfo si = (SampleInfo) info_seq.get(i);
                                 InsufflatorStateObjective obj = (InsufflatorStateObjective) data_seq.get(i);
                                 
                                 System.out.println("Got turn off insufflator");
                                 insufflator.setInsufflatorOff();
                                 //bloodPressure.setDecreasedInterval();

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
                                     obj = new InsufflatorStateObjective();
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
                         	insufflatorStateDataReader.return_loan(data_seq, info_seq);
                         }
                     }
                 });
    }
}
