package org.mdpnp.devices.simulation.clinicianscenario1;

import com.rti.dds.typecode.*;

public class HeartRateIncreaseObjectiveTypeCode {
    public static final TypeCode VALUE = getTypeCode();

    private static TypeCode getTypeCode() {
        TypeCode tc = null;
        int i=0;
        StructMember sm[] = new StructMember[3];

        sm[i]=new StructMember("metric_id",false,(short)-1,true,(TypeCode)ice.MetricIdentifierTypeCode.VALUE,0,false); i++;
        sm[i]=new StructMember("lower",false,(short)-1,false,(TypeCode)TypeCode.TC_FLOAT,1,false); i++;
        sm[i]=new StructMember("upper",false,(short)-1,false,(TypeCode)TypeCode.TC_FLOAT,2,false); i++;

        tc = TypeCodeFactory.TheTypeCodeFactory.create_struct_tc("ice::GlobalAlarmSettingsObjective",ExtensibilityKind.EXTENSIBLE_EXTENSIBILITY,sm);
        return tc;
    }
}
