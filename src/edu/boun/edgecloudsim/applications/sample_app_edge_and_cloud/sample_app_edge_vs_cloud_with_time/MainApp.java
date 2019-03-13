/*
 * Title:        EdgeCloudSim - Main Application
 * 
 * Description:  Main application for Simple App
 *               
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 * Copyright (c) 2017, Bogazici University, Istanbul, Turkey
 */

package edu.boun.edgecloudsim.applications.sample_app_edge_vs_cloud_with_time;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;

import edu.boun.edgecloudsim.core.ScenarioFactory;
import edu.boun.edgecloudsim.core.SimManager;
import edu.boun.edgecloudsim.core.SimSettings;
import edu.boun.edgecloudsim.utils.SimLogger;
import edu.boun.edgecloudsim.utils.SimUtils;

public class MainApp {
	
	/**
	 * Creates main() to run this example
	 */
	public static void main(String[] args) {
		//disable console output of cloudsim library
		Log.disable();
		
		//enable console output and file output of this application
		SimLogger.enablePrintLog();
		
		int iterationNumber = 1;
		String configFile = "";
		String outputFolder = "";
		String edgeDevicesFile = "";
		String applicationsFile = "";
		if (args.length == 5){
			configFile = args[0];
			edgeDevicesFile = args[1];
			applicationsFile = args[2];
			outputFolder = args[3];
			iterationNumber = Integer.parseInt(args[4]);
		}
		else{
			SimLogger.printLine("Simulation setting file, output folder and iteration number are not provided! Using default ones...");
			configFile = "scripts/sample_app_edge_vs_cloud_with_time/config/default_config.properties";
			applicationsFile = "scripts/sample_app_edge_vs_cloud_with_time/config/applications.xml";
			edgeDevicesFile = "scripts/sample_app_edge_vs_cloud_with_time/config/edge_devices.xml";
			outputFolder = "sim_results/ite" + iterationNumber;
		}

		//load settings from configuration file
		SimSettings SS = SimSettings.getInstance();
		if(SS.initialize(configFile, edgeDevicesFile, applicationsFile) == false){
			SimLogger.printLine("cannot initialize simulation settings!");
			System.exit(0);
		}
		
		if(SS.getFileLoggingEnabled()){
			SimLogger.enableFileLog();
			SimUtils.cleanOutputFolder(outputFolder);
		}
		
		
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date SimulationStartDate = Calendar.getInstance().getTime();
		String now = df.format(SimulationStartDate);
		SimLogger.printLine("Simulation started at " + now);
		SimLogger.printLine("----------------------------------------------------------------------");

		for(int j=100; j<=600; j+=100)
		{
			
			//int devices = SimUtils.getRandomNumber(30, 500);
			//int wan_bw = SimUtils.getRandomNumber(1, 7);
			//int wlan_bw = SimUtils.getRandomNumber(20, 50);
			//int vm_on_cloud_host = SimUtils.getRandomNumber(1, 4);
			/*int wlan_bw = 50;
			int wan_bw = 7;
			
			//int index = SimUtils.getRandomNumber(1,4);
			
			/*if(index == 1) {
				edgeDevicesFile = "scripts/sample_app_edge_vs_cloud_with_time/config/edge_devices_3.xml";
			}
			else if(index == 2) {
				edgeDevicesFile = "scripts/sample_app_edge_vs_cloud_with_time/config/edge_devices_6.xml";
			}
			else if(index == 3) {
				edgeDevicesFile = "scripts/sample_app_edge_vs_cloud_with_time/config/edge_devices_9.xml";
			}
			else {
				edgeDevicesFile = "scripts/sample_app_edge_vs_cloud_with_time/config/edge_devices_12.xml";
			}*/
			int devices = j;
			int wan_bw = 7;
			int wlan_bw = 200;
			SS.setWanBandwidth(wan_bw);
			SS.setWlanBandwidth(wlan_bw);
			
			/*SS.setNumOfEdgeHosts(edge_devices);
			SS.setNumOfEdgeDatacenters(edge_devices);
			SS.setNumOfEdgeVMs(edge_devices);
			SS.setNumOfPlaceTypes(edge_devices);
			
			SimLogger.printLine("Number of edge devices: " + SS.getNumOfEdgeHosts());
			SimLogger.printLine("Number of edge devices: " + SS.getNumOfEdgeDatacenters());
			SimLogger.printLine("Number of edge devices: " + SS.getNumOfEdgeVMs());
			SimLogger.printLine("Number of edge devices: " + SS.getNumOfPlaceTypes());*/
			
			
			
			for(int k=0; k<SS.getSimulationScenarios().length; k++)
			{
				for(int i=0; i<SS.getOrchestratorPolicies().length; i++)
				{
					String simScenario = SS.getSimulationScenarios()[k];
					String orchestratorPolicy = SS.getOrchestratorPolicies()[i];
					Date ScenarioStartDate = Calendar.getInstance().getTime();
					now = df.format(ScenarioStartDate);
					
					SimLogger.printLine("Scenario started at " + now);
					SimLogger.printLine("Scenario: " + simScenario + " - Policy: " + orchestratorPolicy + " - #iteration: " + iterationNumber);
					SimLogger.printLine("Duration: " + SS.getSimulationTime()/3600 + " hour(s) - Poisson: " + SS.getTaskLookUpTable()[0][2] + " - #devices: " + j);
					SimLogger.getInstance().simStarted(outputFolder,"SIMRESULT_" + simScenario + "_"  + orchestratorPolicy + "_" + j + "DEVICES");
					SimLogger.printLine("Time Index: " + j);
					try
					{
						// First step: Initialize the CloudSim package. It should be called
						// before creating any entities.
						int num_user = 2;   // number of grid users
						Calendar calendar = Calendar.getInstance();
						boolean trace_flag = false;  // mean trace events
				
						// Initialize the CloudSim library
						CloudSim.init(num_user, calendar, trace_flag, 0.01);
						
						// Generate EdgeCloudsim Scenario Factory
						ScenarioFactory sampleFactory = new SampleScenarioFactory(devices, wan_bw, SS.getSimulationTime(), orchestratorPolicy, simScenario);
						
						// Generate EdgeCloudSim Simulation Manager
						SimManager manager = new SimManager(sampleFactory, devices, simScenario, orchestratorPolicy);
						
						
						
						SimLogger.printLine("Number of mobile devices: " + devices);
						SimLogger.printLine("WAN Bandwidth: " + SS.getWanBandwidth()/1000);
						SimLogger.printLine("WLAN Bandwidth: " + SS.getWlanBandwidth()/1000);
						SimLogger.printLine("VM on Cloud Host: " + SS.getNumOfCloudVMsPerHost());
						SimLogger.printLine("Core for Cloud VM: " + SS.getCoreForCloudVM());
						SimLogger.printLine("Number of edge devices:" + SS.getNumOfEdgeHosts());
						// Start simulation
						manager.startSimulation();
					}
					catch (Exception e)
					{
						SimLogger.printLine("The simulation has been terminated due to an unexpected error");
						e.printStackTrace();
						System.exit(0);
					}
					
					Date ScenarioEndDate = Calendar.getInstance().getTime();
					now = df.format(ScenarioEndDate);
					SimLogger.printLine("Scenario finished at " + now +  ". It took " + SimUtils.getTimeDifference(ScenarioStartDate,ScenarioEndDate));
					SimLogger.printLine("----------------------------------------------------------------------");
				}//End of orchestrators loop
			}//End of scenarios loop
		}//End of mobile devices loop

		Date SimulationEndDate = Calendar.getInstance().getTime();
		now = df.format(SimulationEndDate);
		SimLogger.printLine("Simulation finished at " + now +  ". It took " + SimUtils.getTimeDifference(SimulationStartDate,SimulationEndDate));
	}
}