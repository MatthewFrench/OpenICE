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
package org.mdpnp.apps.cs1;

import java.awt.Color;

import org.mdpnp.apps.testapp.vital.Vital;
import org.mdpnp.apps.testapp.vital.VitalModel;

/**
 * @author Jeff Plourde
 *
 */
public enum CS1VitalSign {
	BPState("BP State", "", new String[] { 
   			rosetta.MDC_PRESS_CUFF.VALUE 
   		}, 0f, 10000f, 0f, 10000f, 0f, 10000f, 0L, 5000L, Color.pink),
	Systolic("Systolic", "mmHg", new String[] { 
   			rosetta.MDC_PRESS_CUFF_SYS.VALUE 
   		}, 0f, 200f, 0f, 200f, 0f, 200f, 0L, 5000L, Color.pink),
	Disastolic("Disastolic", "mmHg", new String[] { 
   			rosetta.MDC_PRESS_CUFF_DIA.VALUE 
   		}, 0f, 200f, 0f, 200f, 0f, 200f, 0L, 5000L, Color.pink),
   		/*
	NextInflation("Next Inflation", "min", new String[] { 
   			rosetta.MDC_PRESS_CUFF_NEXT_INFLATION.VALUE 
   		}, 95f, 100f, 85f, 100f, 50f, 100f, 5000L, 5000L, Color.green),
           	Inflation("Cuff Inflation", "mmHg", new String[] { 
           			rosetta.MDC_PRESS_CUFF_INFLATION.VALUE 
           		}, 95f, 100f, 85f, 100f, 50f, 100f, 5000L, 5000L, Color.pink), 
           		*/
   		
           	PulseRate("Pulse Rate", "bpm", new String[] { 
           			rosetta.MDC_PULS_RATE_NON_INV.VALUE
           		}, 0f, 100f, 0f, 100f, 0f, 100f, 0L, 5000L, Color.yellow), 
           		
           	InsufflatorPressure("Insufflator Pressure", "mmHg", new String[] { 
           			rosetta.CLINICIAN_SCENARIO_1_INSUFFLATOR_PRESSURE.VALUE 
           		}, -1f, 20f, 0f, 20f, 0f, 120f, 0L, 100L, Color.yellow), 
           	InsufflatorStatus("Insufflator Status", "1 = On", new String[] { 
           			rosetta.CLINICIAN_SCENARIO_1_INSUFFLATOR_STATUS.VALUE 
           		}, -1f, 2f, -1f, 2f, -1f, 50f, 0L, 100L, Color.black);
    ;

    CS1VitalSign(String label, String units, String[] metric_ids, Float startingLow, Float startingHigh, Float criticalLow, Float criticalHigh,
            float minimum, float maximum, Long valueMsWarningLow, Long valueMsWarningHigh, Color color) {
        this.label = label;
        this.units = units;
        this.metric_ids = metric_ids;
        this.startingLow = startingLow;
        this.startingHigh = startingHigh;
        this.minimum = minimum;
        this.maximum = maximum;
        this.criticalLow = criticalLow;
        this.criticalHigh = criticalHigh;
        this.valueMsWarningLow = valueMsWarningLow;
        this.valueMsWarningHigh = valueMsWarningHigh;
        this.color = color;
    }

    public Vital addToModel(VitalModel vitalModel) {
        return vitalModel.addVital(label, units, metric_ids, startingLow, startingHigh, criticalLow, criticalHigh, minimum, maximum,
                valueMsWarningLow, valueMsWarningHigh, color);
    }

    private final String label, units;;
    private final String[] metric_ids;
    private final Float startingLow, startingHigh, criticalLow, criticalHigh;
    private final Long valueMsWarningLow, valueMsWarningHigh;
    private final float minimum, maximum;
    private final Color color;
}
