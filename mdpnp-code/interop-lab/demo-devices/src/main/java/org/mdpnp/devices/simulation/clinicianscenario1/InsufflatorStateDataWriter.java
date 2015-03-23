package org.mdpnp.devices.simulation.clinicianscenario1;

import com.rti.dds.infrastructure.Time_t;
import com.rti.dds.infrastructure.WriteParams_t;
import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.publication.DataWriterImpl;
import com.rti.dds.publication.DataWriterListener;
import com.rti.dds.topic.TypeSupportImpl;

public class InsufflatorStateDataWriter extends DataWriterImpl {
    // -----------------------------------------------------------------------
    // Public Methods
    // -----------------------------------------------------------------------

    public InstanceHandle_t register_instance(InsufflatorStateObjective instance_data) {
        return register_instance_untyped(instance_data);
    }
    
    
    public InstanceHandle_t register_instance_w_timestamp(InsufflatorStateObjective instance_data,
                                                 Time_t source_timestamp) {
        return register_instance_w_timestamp_untyped(
            instance_data, source_timestamp);
    }


    public InstanceHandle_t register_instance_w_params(InsufflatorStateObjective instance_data,
                                                 WriteParams_t params) {
        return register_instance_w_params_untyped(
            instance_data, params);
    }
    

    public void unregister_instance(InsufflatorStateObjective instance_data,
                                     InstanceHandle_t handle) {
        unregister_instance_untyped(instance_data, handle);
    }
    
    
    public void unregister_instance_w_timestamp(InsufflatorStateObjective instance_data,
            InstanceHandle_t handle, Time_t source_timestamp) {
        
        unregister_instance_w_timestamp_untyped(
            instance_data, handle, source_timestamp);
    }


    public void unregister_instance_w_params(InsufflatorStateObjective instance_data,
                                             WriteParams_t params) {
        
        unregister_instance_w_params_untyped(
            instance_data, params);
    }
    
    
    public void write(InsufflatorStateObjective instance_data, InstanceHandle_t handle) {
        write_untyped(instance_data, handle);
    }
    
    
    public void write_w_timestamp(InsufflatorStateObjective instance_data,
            InstanceHandle_t handle, Time_t source_timestamp) {

        write_w_timestamp_untyped(instance_data, handle, source_timestamp);
    }


    public void write_w_params(InsufflatorStateObjective instance_data,
                               WriteParams_t params) {

        write_w_params_untyped(instance_data, params);
    }
    
    
    public void dispose(InsufflatorStateObjective instance_data, InstanceHandle_t instance_handle){
        dispose_untyped(instance_data, instance_handle);
    }
    
    
    public void dispose_w_timestamp(InsufflatorStateObjective instance_data,
            InstanceHandle_t instance_handle, Time_t source_timestamp) {

        dispose_w_timestamp_untyped(
            instance_data, instance_handle, source_timestamp);
    }

    
    public void dispose_w_params(InsufflatorStateObjective instance_data,
                                 WriteParams_t params) {

        dispose_w_params_untyped(instance_data, params);
    }

       
    public void get_key_value(InsufflatorStateObjective key_holder, InstanceHandle_t handle) {
        get_key_value_untyped(key_holder, handle);
    }
    

    public InstanceHandle_t lookup_instance(InsufflatorStateObjective key_holder) {
        return lookup_instance_untyped(key_holder);
    }

    // -----------------------------------------------------------------------
    // Package Methods
    // -----------------------------------------------------------------------

    // --- Constructors: -----------------------------------------------------
    
    /*package*/ InsufflatorStateDataWriter(long native_writer, DataWriterListener listener,
                              int mask, TypeSupportImpl type) {
        super(native_writer, listener, mask, type);
    }
}
