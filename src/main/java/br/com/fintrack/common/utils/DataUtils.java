package br.com.fintrack.common.utils;

import br.com.fintrack.common.exceptions.UsersException;

import java.time.LocalDate;

public class DataUtils {
    public static LocalDate parseDate(String date) {
        try {
            return LocalDate.parse(date);
        } catch (Exception ex) {
            throw new UsersException("Formato de data inválido. Use yyyy-MM-dd");
        }
    }
}
