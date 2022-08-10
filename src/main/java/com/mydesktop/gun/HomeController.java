package com.mydesktop.gun;


import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import oshi.SystemInfo;
import oshi.hardware.Baseboard;
import oshi.hardware.CentralProcessor;
import oshi.hardware.ComputerSystem;
import oshi.hardware.Firmware;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.UsbDevice;
import oshi.software.os.OperatingSystem;
import oshi.util.FormatUtil;


/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	
	public static final Logger LOG = LoggerFactory.getLogger(HomeController.class);
	
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		
		 LOG.info("시스템 초기화 중...");
	        SystemInfo si = new SystemInfo();

	        HardwareAbstractionLayer hal = si.getHardware();
	        OperatingSystem os = si.getOperatingSystem();

	        System.out.println("운영체제" + os);
	        model.addAttribute("os", os);

	    	LOG.info("컴퓨터 시스템 확인 중...");
			printComputerSystem(hal.getComputerSystem(), model);      

	        LOG.info("메모리 확인중...");
	        printMemory(hal.getMemory(), model);

	        LOG.info("CPU 확인중...");
	        printCpu(hal.getProcessor(), model);

//	        LOG.info("센서 확인중...");
//	        printSensors(hal.getSensors());

//	        LOG.info("전원 확인중...");
//	        printPowerSources(hal.getPowerSources());
//
//	        LOG.info("디스크 확인중...");
//	        printDisks(hal.getDiskStores());
//
//	        LOG.info("파일 시스템 확인중...");
//	        printFileSystem(os.getFileSystem());
//
//	        LOG.info("네트워크 인터페이스 확인중...");
//	        printNetworkInterfaces(hal.getNetworkIFs());
//
//	        LOG.info("네트워크 매개변수 확인...");
//	        printNetworkParameters(os.getNetworkParams());
//
//	        // 하드웨어: 디스플레이 장치
//	        LOG.info("디스플레이 확인중...");
//	        printDisplays(hal.getDisplays());
//
//	        // 하드웨어: USB 장치
//	        LOG.info("USB 장치확인...");
//	        printUsbDevices(hal.getUsbDevices(true));	
		
		return "main";
	}


	private void printComputerSystem(ComputerSystem computerSystem, Model model) {
		
		model.addAttribute("manufacturer", computerSystem.getManufacturer());
        model.addAttribute("model", computerSystem.getModel());
        model.addAttribute("serialnumber", computerSystem.getSerialNumber());
        
        final Firmware firmware = computerSystem.getFirmware();
        model.addAttribute("firmwaremanufacturer", firmware.getManufacturer());
        model.addAttribute("firmwareversion", firmware.getVersion());
       
        final Baseboard baseboard = computerSystem.getBaseboard();
        model.addAttribute("baseboardmanufacturer", baseboard.getManufacturer());
        model.addAttribute("baseboardversion", baseboard.getVersion());
        model.addAttribute("baseboardserialnumber", baseboard.getSerialNumber());  
    }
	
	private void printCpu(CentralProcessor processor, Model model) {
		
		model.addAttribute("CPU", processor);
	}


	private void printMemory(GlobalMemory memory, Model model) {
	
		model.addAttribute("Availablememory", FormatUtil.formatBytes(memory.getTotal() - memory.getAvailable()));
		model.addAttribute("Totalmemory", FormatUtil.formatBytes(memory.getTotal()));
		model.addAttribute("memoryper", (int)(memory.getTotal()/memory.getAvailable()));
		
		
	}


	}


	
	
	
	
