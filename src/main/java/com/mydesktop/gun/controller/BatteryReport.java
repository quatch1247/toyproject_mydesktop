package com.mydesktop.gun.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
//배터리 정보
@RequestMapping(value = "/batteryreport", method = {RequestMethod.GET, RequestMethod.POST })
public class BatteryReport {
	
	public static final Logger LOG = LoggerFactory.getLogger(BatteryReport.class);
	public String main() throws IOException {
		
		LOG.info("배터리 확인중");
		
	//cmd접속
	String cmdCommand = "cmd /c";
	String batteryCommand = "powercfg/batteryreport";
	
	// 컴퓨터 정보
	String computerName = "COMPUTER NAME";
	String systemModel = "SYSTEM PRODUCT NAME";
	String bios = "BIOS";
	String reportTime = "REPORT TIME";
	String cyclecount = "CYClE COUNT";
	
	Map<String, String> overView = new LinkedHashMap<String, String>();

	// 배터리정보
	String batteryName = "NAME";
	String manufacture = "MANUFACTURE";
	String serialNumber = "SERIAL NUMBER";
	String chemistry = "CHEMISTRY";
	String designCapacity = "DESIGN CAPACITY";
	String fullChargeCapacity = "FULL CHARGE CAPACITY";
	String percentage = "PERCENTAGE";
	Map<String, String> batteryInfo = new LinkedHashMap<String, String>();

	
	// 비교
	List<String[]> currentCapac = new LinkedList<String[]>();
	
	// cmd 연결
	Runtime runtime = Runtime.getRuntime();
	Process process = runtime.exec(cmdCommand + batteryCommand);
	InputStream input = process.getInputStream();
	
	// 경로 읽기
	BufferedReader reader = new BufferedReader(new InputStreamReader(input));
	String result = reader.readLine();
	reader.close();
	String path = "path";

		
	// 문자열 앞 자르기
	int firstIndex = result.toString().indexOf(path) + path.length() + 27;
	// 문자열 뒤 자르기
	int lastIndext = result.toString().length() - 15;

	String url = result.toString().substring(firstIndex, lastIndext);

	 // 경로로 파일 생성
	File batteryReportHTML = new File(url);
	Document batteryHTML = Jsoup.parse(batteryReportHTML, null);


	// html에서 td로 묶인 요소 분리
	Elements elements = batteryHTML.select("td");

	// html에서 가져온 모든 데이터를 저장할 새 문자열
	String trimReport = "";

	// BatteryReport.html의 모든 데이터로 초기화된 trimReport
	for (Element e : elements) {
		trimReport = trimReport + e.ownText() + "\n";
	}

	// scan and temp to check line by line in the battery Report
	Scanner scan = new Scanner(trimReport);
	String temp;

	// 배터리가 생성된 시간을 저장하는 timeStamp
	String timeStamp = new SimpleDateFormat("HH:mm:ss yyyy/MM/dd ").format(Calendar.getInstance().getTime());

	// REPORT TIME전 컴퓨터 정보 섹션 스캔
	// 저장된 컴퓨터 정보 HashMap overView
	while (!(temp = scan.nextLine()).equals("REPORT TIME")) {
		if (temp.equals(computerName)) {
			overView.put(computerName, scan.nextLine());
		}
		if (temp.equals(systemModel)) {
			overView.put(systemModel, scan.nextLine());
		}
		if (temp.equals(bios)) {
			overView.put(bios, scan.nextLine());
		}
	}
	
		// 리포트타임
		overView.put(reportTime, timeStamp);

		
		// 배터리 정보가 표시될 때까지 스캔
		while (!(scan.nextLine().contains("BATTERY"))) {

		}
		
		// 해시맵에 배터리 관련 정보 넣기
		scan.nextLine();
		batteryInfo.put(batteryName, scan.nextLine());
		scan.nextLine();
		batteryInfo.put(manufacture, scan.nextLine());
		scan.nextLine();
		batteryInfo.put(serialNumber, scan.nextLine());
		scan.nextLine();
		batteryInfo.put(chemistry, scan.nextLine());
		scan.nextLine();
		batteryInfo.put(designCapacity, scan.nextLine());
		scan.nextLine();
		batteryInfo.put(fullChargeCapacity, scan.nextLine());
		batteryInfo.put(percentage, percentage(batteryInfo.get(fullChargeCapacity), batteryInfo.get(designCapacity)));
		scan.nextLine();
		batteryInfo.put(cyclecount, scan.nextLine());
		
		ArrayList<String> batteryinfo = new ArrayList<String>();
		batteryinfo.add(batteryInfo.get(batteryName));
		batteryinfo.add(batteryInfo.get(manufacture));
		batteryinfo.add(batteryInfo.get(serialNumber));
		batteryinfo.add(batteryInfo.get(chemistry));
		batteryinfo.add(batteryInfo.get(designCapacity));
		batteryinfo.add(batteryInfo.get(fullChargeCapacity));
		batteryinfo.add(batteryInfo.put(percentage, percentage(batteryInfo.get(fullChargeCapacity), batteryInfo.get(designCapacity))));
		batteryinfo.add(batteryInfo.get(cyclecount));
		
		System.out.println("품명 : "+ batteryInfo.get(batteryName));
		System.out.println("제조사 : "+ batteryInfo.get(manufacture));
		System.out.println("시리얼넘버 : "+ batteryInfo.get(serialNumber));
		System.out.println("배터리 타입 : "+ batteryInfo.get(chemistry));
		System.out.println("추정 용량 : "+ batteryInfo.get(designCapacity));
		System.out.println("설계 용량 : "+ batteryInfo.get(fullChargeCapacity));
		System.out.println("예상 배터리 용량 : " + batteryInfo.put(percentage, percentage(batteryInfo.get(fullChargeCapacity), batteryInfo.get(designCapacity))));
		System.out.println("사이클 수 : "+ batteryInfo.get(cyclecount));
		System.out.println();
		System.out.println("컴퓨터 이름 : " + overView.get(computerName));
		System.out.println("바이오스 : " + overView.get(bios));
		System.out.println("보고시간 : "+ overView.get(reportTime));
		

		
//		Date, currentCapcity, designCapacity의 생성 및 배열
//		데이터는 다음에 의해 저장.
//		설계용량, 추청용량은 위의 batteryInfo에서 가져옴
		while (true) {
		
			String[] tempList = new String[3];
			String date = scan.nextLine();
			if (date.length() == 0) {
				break;
			}
			tempList[0] = date;
			String current = scan.nextLine();
			tempList[1] = current;
			tempList[2] = batteryInfo.get(designCapacity);
			// Add to a List<String[]> currentCapac
			currentCapac.add(tempList);
			scan.nextLine();
		}

		batteryReportHTML.delete();
		
		return "redirect:/main1";
	}

	public static int evaluate(String input) {
		input = input.replace(",", "");
		input = input.replace("mWh", "");
		input = input.trim();
		return Integer.parseInt(input);
	}
	public static String percentage(String string1, String string2) {
		int num1 = evaluate(string1);
		int num2 = evaluate(string2);
		double ratio = num1 * 100 / num2;
		String percentage = (ratio) + "%";
		return percentage;
	}
}
