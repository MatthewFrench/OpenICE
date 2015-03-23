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
package org.mdpnp.guis.swing;

import ice.Numeric;
import ice.NumericDataReader;
import ice.SampleArray;
import ice.SampleArrayDataReader;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.mdpnp.guis.waveform.NumericWaveformSource;
import org.mdpnp.guis.waveform.SampleArrayWaveformSource;
import org.mdpnp.guis.waveform.WaveformPanel;
import org.mdpnp.guis.waveform.WaveformPanelFactory;
import org.mdpnp.rtiapi.data.DeviceDataMonitor;
import org.mdpnp.rtiapi.data.InstanceModel;
import org.mdpnp.rtiapi.data.InstanceModelListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.subscription.SampleInfo;

@SuppressWarnings("serial")
/**
 * @author Jeff Plourde
 *
 */
public class ClinicianScenario1InsufflatorPanel extends DevicePanel {

    @SuppressWarnings("unused")
    private JLabel spo2, heartrate, spo2Label, heartrateLabel;
    private JLabel spo2Low, spo2Up, heartrateLow, heartrateUp;
    private JPanel spo2Bounds, heartrateBounds;
    private JPanel spo2Panel, heartratePanel;
    private WaveformPanel pulsePanel, plethPanel;
    private JLabel time;
    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    protected void buildComponents() {
        spo2Bounds = new JPanel();
        spo2Bounds.setOpaque(false);
        spo2Bounds.setLayout(new GridLayout(3, 1));
        spo2Bounds.add(spo2Up = new JLabel("--"));
        spo2Bounds.add(spo2Low = new JLabel("--"));
        spo2Bounds.add(spo2Label = new JLabel(""));
        spo2Up.setVisible(false);
        spo2Low.setVisible(false);

        spo2Panel = new JPanel();
        spo2Panel.setOpaque(false);
        spo2Panel.setLayout(new BorderLayout());
        spo2Panel.add(new JLabel("Insufflator On = 1/Off = 0"), BorderLayout.NORTH);
        spo2Panel.add(spo2 = new JLabel("----"), BorderLayout.CENTER);
        spo2.setHorizontalAlignment(JLabel.RIGHT);
        spo2.setHorizontalTextPosition(SwingConstants.RIGHT);

        spo2.setBorder(new EmptyBorder(5, 5, 5, 5));
        spo2Panel.add(spo2Bounds, BorderLayout.EAST);

        heartrateBounds = new JPanel();
        heartrateBounds.setOpaque(false);
        heartrateBounds.setLayout(new GridLayout(3, 1));
        heartrateBounds.add(heartrateUp = new JLabel("--"));
        heartrateBounds.add(heartrateLow = new JLabel("--"));
        heartrateBounds.add(heartrateLabel = new JLabel("mmHG"));
        heartrateUp.setVisible(false);
        heartrateLow.setVisible(false);

        heartratePanel = new JPanel();
        heartratePanel.setOpaque(false);
        heartratePanel.setLayout(new BorderLayout());
        JLabel lbl;
        heartratePanel.add(lbl = new JLabel("Pressure"), BorderLayout.NORTH);
        int w = lbl.getFontMetrics(lbl.getFont()).stringWidth("RespiratoryRate");
        lbl.setMinimumSize(new Dimension(w, lbl.getMinimumSize().height));
        lbl.setPreferredSize(lbl.getMinimumSize());
        heartratePanel.add(heartrate = new JLabel("----"), BorderLayout.CENTER);
        heartrate.setHorizontalTextPosition(SwingConstants.RIGHT);
        heartrate.setBorder(new EmptyBorder(5, 5, 5, 5));
        heartrate.setHorizontalAlignment(JLabel.RIGHT);
        heartratePanel.add(heartrateBounds, BorderLayout.EAST);

        SpaceFillLabel.attachResizeFontToFill(this, spo2, heartrate);

        WaveformPanelFactory fact = new WaveformPanelFactory();

        plethPanel = fact.createWaveformPanel();
        pulsePanel = fact.createWaveformPanel();

        JPanel upper = new JPanel(new GridLayout(2, 1));
        upper.setOpaque(false);
        upper.add(label("Insufflator State (On/Off)", plethPanel.asComponent()));
        upper.add(label("Pressure", pulsePanel.asComponent()));

        JPanel east = new JPanel(new GridLayout(2, 1));
        east.add(spo2Panel);
        east.add(heartratePanel);

        setLayout(new BorderLayout());
        add(upper, BorderLayout.CENTER);
        add(east, BorderLayout.EAST);

        add(label("Insufflator Last Sample: ", time = new JLabel("TIME"), BorderLayout.WEST), BorderLayout.SOUTH);

        setForeground(Color.cyan);
        setBackground(Color.black);
        setOpaque(true);
    }

    public ClinicianScenario1InsufflatorPanel() {
        setBackground(Color.black);
        setOpaque(true);
        buildComponents();
        plethPanel.setSource(plethWave);
        pulsePanel.setSource(pulseWave);
        
        plethPanel.start();
        pulsePanel.start();
    }

    private NumericWaveformSource plethWave;
    private NumericWaveformSource pulseWave;

    @Override
    public void destroy() {
        plethPanel.setSource(null);
        pulsePanel.setSource(null);
        plethPanel.stop();
        pulsePanel.stop();
        
        if(deviceMonitor != null) {
            deviceMonitor.getNumericModel().removeListener(numericListener);
        }
        super.destroy();
    }

    public static boolean supported(Set<String> names) {
        return names.contains(rosetta.CLINICIAN_SCENARIO_1_INSUFFLATOR_STATUS.VALUE) && names.contains(rosetta.CLINICIAN_SCENARIO_1_INSUFFLATOR_PRESSURE.VALUE);// &&
        // names.contains(ice.Physio._MDC_PULS_OXIM_PLETH);
    }

    @Override
    public void set(DeviceDataMonitor deviceMonitor) {
        super.set(deviceMonitor);
        deviceMonitor.getNumericModel().iterateAndAddListener(numericListener);
    }
    
    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(ClinicianScenario1InsufflatorPanel.class);
    private final Date date = new Date();
    
    private final InstanceModelListener<ice.Numeric, ice.NumericDataReader> numericListener = new InstanceModelListener<ice.Numeric, ice.NumericDataReader>() {

        @Override
        public void instanceAlive(InstanceModel<Numeric, NumericDataReader> model, NumericDataReader reader, Numeric data, SampleInfo sampleInfo) {
            if (rosetta.CLINICIAN_SCENARIO_1_INSUFFLATOR_PRESSURE.VALUE.equals(data.metric_id)) {
                if(null == pulseWave) {
                    pulseWave = new NumericWaveformSource(model.getReader(), data);
                    pulsePanel.setSource(pulseWave);
                }
                date.setTime(1000L * sampleInfo.source_timestamp.sec + sampleInfo.source_timestamp.nanosec / 1000000L);
                time.setText(dateFormat.format(date));
            }
            if (rosetta.CLINICIAN_SCENARIO_1_INSUFFLATOR_STATUS.VALUE.equals(data.metric_id)) {
                if(null == pulseWave) {
                    plethWave = new NumericWaveformSource(model.getReader(), data);
                    plethPanel.setSource(pulseWave);
                }
                date.setTime(1000L * sampleInfo.source_timestamp.sec + sampleInfo.source_timestamp.nanosec / 1000000L);
                time.setText(dateFormat.format(date));
            }
        }

        @Override
        public void instanceNotAlive(InstanceModel<Numeric, NumericDataReader> model, NumericDataReader reader, Numeric keyHolder,
                SampleInfo sampleInfo) {
        }

        @Override
        public void instanceSample(InstanceModel<Numeric, NumericDataReader> model, NumericDataReader reader, Numeric data, SampleInfo sampleInfo) {
            setInt(data, rosetta.CLINICIAN_SCENARIO_1_INSUFFLATOR_STATUS.VALUE, spo2, null);
            setInt(data, rosetta.CLINICIAN_SCENARIO_1_INSUFFLATOR_PRESSURE.VALUE, heartrate, null);
            date.setTime(1000L * sampleInfo.source_timestamp.sec + sampleInfo.source_timestamp.nanosec / 1000000L);
            time.setText(dateFormat.format(date));
        }
        
    };
}
