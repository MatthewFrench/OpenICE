package org.mdpnp.devices.simulation.clinicianscenario1;

import com.rti.dds.infrastructure.*;
import com.rti.dds.infrastructure.Copyable;

import java.io.Serializable;
import com.rti.dds.cdr.CdrHelper;


public class InsufflatorStateObjective implements Copyable, Serializable
{

    public String metric_id = ""; /* maximum length = (64) */
    public float lower = 0;
    public float upper = 0;


    public InsufflatorStateObjective() {

    }


    public InsufflatorStateObjective(InsufflatorStateObjective other) {

        this();
        copy_from(other);
    }



    public static Object create() {
    	InsufflatorStateObjective self;
        self = new InsufflatorStateObjective();
         
        self.clear();
        
        return self;
    }

    public void clear() {
        
        metric_id = "";
            
        lower = 0;
            
        upper = 0;
            
    }

    public boolean equals(Object o) {
                
        if (o == null) {
            return false;
        }        
        
        

        if(getClass() != o.getClass()) {
            return false;
        }

        InsufflatorStateObjective otherObj = (InsufflatorStateObjective)o;



        if(!metric_id.equals(otherObj.metric_id)) {
            return false;
        }
            
        if(lower != otherObj.lower) {
            return false;
        }
            
        if(upper != otherObj.upper) {
            return false;
        }
            
        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result += metric_id.hashCode();
                
        __result += (int)lower;
                
        __result += (int)upper;
                
        return __result;
    }
    

    /**
     * This is the implementation of the <code>Copyable</code> interface.
     * This method will perform a deep copy of <code>src</code>
     * This method could be placed into <code>GlobalAlarmSettingsObjectiveTypeSupport</code>
     * rather than here by using the <code>-noCopyable</code> option
     * to rtiddsgen.
     * 
     * @param src The Object which contains the data to be copied.
     * @return Returns <code>this</code>.
     * @exception NullPointerException If <code>src</code> is null.
     * @exception ClassCastException If <code>src</code> is not the 
     * same type as <code>this</code>.
     * @see com.rti.dds.infrastructure.Copyable#copy_from(java.lang.Object)
     */
    public Object copy_from(Object src) {
        

    	InsufflatorStateObjective typedSrc = (InsufflatorStateObjective) src;
    	InsufflatorStateObjective typedDst = this;

        typedDst.metric_id = typedSrc.metric_id;
            
        typedDst.lower = typedSrc.lower;
            
        typedDst.upper = typedSrc.upper;
            
        return this;
    }


    
    public String toString(){
        return toString("", 0);
    }
        
    
    public String toString(String desc, int indent) {
        StringBuffer strBuffer = new StringBuffer();        
                        
        
        if (desc != null) {
            CdrHelper.printIndent(strBuffer, indent);
            strBuffer.append(desc).append(":\n");
        }
        
        
        CdrHelper.printIndent(strBuffer, indent+1);            
        strBuffer.append("metric_id: ").append(metric_id).append("\n");
            
        CdrHelper.printIndent(strBuffer, indent+1);            
        strBuffer.append("lower: ").append(lower).append("\n");
            
        CdrHelper.printIndent(strBuffer, indent+1);            
        strBuffer.append("upper: ").append(upper).append("\n");
            
        return strBuffer.toString();
    }
    
}

