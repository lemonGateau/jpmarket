package market.jpmarket;

import java.time.LocalDate;
import java.util.ArrayList;

public class Config {
	public static LocalDate firstDate = LocalDate.parse("2024-03-14");
	public static LocalDate lastDate   = LocalDate.parse("2025-03-14");
	
	public static ArrayList<String> stockCodes = new ArrayList<>() {
		{
			add("1605");
			add("4751");
			add("5411");
			add("7261");
			add("7267");
			add("7272");
			add("8058");
			add("9434");
		}
	};
	
	public static Integer assetYen = 500000;
}
