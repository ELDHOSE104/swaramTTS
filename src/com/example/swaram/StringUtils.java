package com.example.swaram;

public class StringUtils {
	
	static final String[] leftChars = {"െ,േ,ൈ"};
	static final String[] rightChars = {"ാ,ി,ീ,�?,ൂ,ൃ,ൗ"};

	public static String removeInvalidCharacters(String text) {

		// text = text.replaceAll("^\\w", "");
		// text = text.replaceAll("\\x20-\\x7e", "");
		text = text.replace("|", "");
		text = text.replace("'", "");
		text = text.replace("©", "");
		text = text.replace("-", "");
		text = text.replace("/", "");
		text = text.replace("\\", "");
		text = text.replace("_", "");
		text = text.replace(",", "");
		text = text.replace("&", "");
		text = text.replace("@", "");
		text = text.replace("*", "");
		text = text.replace("(", "");
		text = text.replace(")", "");
		text = text.replace("#", "");
		text = text.replace("%", "");
		text = text.replace("^", "");
		text = text.replace("=", "");
		text = text.replace(";", "");
		text = text.replace("`", "");
		text = text.replace("?", "");
		text = text.replace(">", "");
		text = text.replace("<", "");
		text = text.replace("~", "");
		text = text.replace("|", "");
		text = text.replace("©", "");
		text = text.replace(":", "");
		text = text.replace("]", "");
		text = text.replace("[", "");
		text = text.replace("}", "");
		text = text.replace("{", "");
		text = text.replace("_", "");
		text = text.replaceAll("[/$]", "");

		text = text.replaceAll("[|?*<\":>+\\\\/']", "");
		text = text.replaceAll("[A-Za-z0-9]", "");
		text = text.replace("!", "");
		text = text.replace("+", "");

		return text;
	}
	
	public static String getFileToPlay(Character c)
	{
		return null;
		
	}
}
