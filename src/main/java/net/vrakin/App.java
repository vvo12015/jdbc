package net.vrakin;

import lombok.extern.slf4j.Slf4j;
import net.vrakin.dao.ApartmentDao;
import net.vrakin.dao.DBConnector;
import net.vrakin.dao.RegionDao;
import net.vrakin.dto.Apartment;
import net.vrakin.dto.Region;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

/**
 * Apartment manager
 * This program is crud for apartment
 *
 */
@Slf4j
public class App 
{

    static Connection conn;

    private static final RegionDao regionDao = new RegionDao();;
    private static final ApartmentDao apartmentDao = new ApartmentDao();

    public static void main( String[] args ) {

    }
}
