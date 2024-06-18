package com.events.eventosUfsm.shared;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;


public class Output {

  private static final String RED = "\u001B[31m"; // Red
  private static final String GREEN = "\u001B[95m"; // Green
  private static final String YELLOW = "\u001B[33m"; // Yellow
  private static final String BLUE = "\u001B[92m"; // Blue
  private static final String PURPLE = "\u001B[35m"; // Purple
  private static final String RESET = "\u001B[0m"; // Reset to default color

  private static final String STRING_PURP = "\u001B[38;5;20m"; // Grey
  private static final String STRING_BLUE = "\u001B[38;5;44m"; // Grey

  private static LocalDate anotherSummerDay = LocalDate.now();
  private static LocalTime anotherTime = LocalTime.now();
  private static ZonedDateTime zonedDateTime = ZonedDateTime.of(anotherSummerDay, anotherTime,
      ZoneId.of("America/Sao_Paulo"));
  private static String formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).format(zonedDateTime);

  public static void fail(String string) {
    string = "[%s] %sFAIL%s %s".formatted(formatter, RED, RESET, string);
    System.out.println(string);
  }

  public static void done(String string) {
    string = "[%s] %sDONE%s %s%s%s".formatted(formatter, BLUE, RESET, STRING_BLUE, string, RESET);
    System.out.println(string);
  }

  public static void info(String string) {
    string = "[%s] %sINFO%s %s%s%s".formatted(formatter, GREEN, RESET, STRING_PURP, string, RESET);
    System.out.println(string);
  }

  public static void here(String string) {
    string = "[%s] %sHERE%s %s".formatted(formatter, YELLOW, RESET, string);
    System.out.println(string);
  }

  public static void warn(String string) {
    string = "[%s] %sWARN%s %s".formatted(formatter, YELLOW, RESET, string);
    System.out.println(string);
  }

  public static void custom(String string) {
    string = "[%s] %sCUSTOM%s %s".formatted(formatter, PURPLE, RESET, string);
    System.out.println(string);
  }

  public static void log(String message) {
    System.out.println(message);
  }

  public static void logBox(String message) {
    List<String> lines = Arrays.asList(message.split("\n"));

    int width = lines.stream().max(Comparator.comparingInt(String::length)).get().length();

    System.out.println("╭" + "─".repeat(width + 2) + "╮");

    for (String line : lines) {

      if (line.startsWith("---") || line.endsWith("---")) {
        System.out.println("├" + "─".repeat(width + 2) + "┤");
        continue;
      }

      System.out.println("│ " + ("%-" + width + "s").formatted(line) + " │");
    }

    System.out.println("╰" + "─".repeat(width + 2) + "╯");
  }

}
