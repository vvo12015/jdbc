package net.vrakin.dao;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.vrakin.dto.Apartment;
import net.vrakin.dto.Region;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Slf4j
@NoArgsConstructor
public class ApartmentDao implements Dao<Apartment> {

    public static final int COLUMN_INDEX_UPDATE_ID = 5;
    public static final int COLUMN_INDEX_ID = 1;
    public static final int COLUMN_INDEX_CAPACITY = 2;
    public static final int COLUMN_INDEX_PRICE = 3;
    public static final int COLUMN_INDEX_REGION = 4;
    public static final int PARAMETER_INDEX_ADDRESS_BY_ADDRESS = 1;
    public static final int PARAMETER_INDEX_ADDRESS_BY_ADDRESS_REGION = 2;
    public static final int PARAMETER_INDEX_REGION_REF = 1;
    public static final int PARAMETER_INDEX_PRICE_START_BY_REGION = 2;
    public static final int PARAMETER_INDEX_PRICE_END_BY_REGION = 3;
    public static final int PARAMETER_INDEX_CAPACITY_START_BY_CAPACITY_PRICE = 1;
    public static final int PARAMETER_INDEX_CAPACITY_END_BY_CAPACITY_PRICE = 2;
    public static final int PARAMETER_INDEX_PRICE_START_BY_CAPACITY_PRICE = 3;
    public static final int PARAMETER_INDEX_PRICE_END_BY_CAPACITY_PRICE = 4;
    public static final int PARAMETER_INDEX_REGION_REGION_BY_CAPACITY_PRICE = 1;
    public static final int PARAMETER_INDEX_CAPACITY_START_BY_REGION_CAPACITY_PRICE = 2;
    public static final int PARAMETER_INDEX_CAPACITY_END_BY_REGION_CAPACITY_PRICE = 3;
    public static final int PARAMETER_INDEX_PRICE_START_BY_REGION_CAPACITY_PRICE = 4;
    public static final int PARAMETER_INDEX_PRICE_END_BY_REGION_CAPACITY_PRICE = 5;
    public final DBConnector dbConnector = new DBConnector();

    private final RegionDao regionDao = new RegionDao();

    @Override
    public Apartment add(Apartment apartment) {
        Long id = null;


        try(Connection conn = dbConnector.getConnection();
            PreparedStatement ps = conn.prepareStatement(Apartment.INSERT_APARTMENT,
                    Statement.RETURN_GENERATED_KEYS)){

            setValueForPSWithoutID(apartment, ps);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                id = rs.getLong("GENERATED_KEY");
            }

        }catch (SQLException e){
            log.error(ExceptionUtils.getStackTrace(e));
        }
        finally {
            dbConnector.closeConnection();
        }

        return getById(id);
    }

    @Override
    public Apartment update(Apartment apartment) {
        Long id = null;

        if (!Objects.isNull(apartment.getApartmentId())) {
            id = apartment.getApartmentId();
        }else{
            throw new IllegalArgumentException("apartment id is null");
        }

        try(Connection conn = dbConnector.getConnection();
            PreparedStatement ps = conn.prepareStatement(Apartment.UPDATE_APARTMENT)){

            setValueForPSWithoutID(apartment, ps);
            ps.setLong(COLUMN_INDEX_UPDATE_ID, apartment.getApartmentId());
            ps.executeUpdate();
        }catch (SQLException e){
            log.error(ExceptionUtils.getStackTrace(e));
        }
        finally {
            dbConnector.closeConnection();
        }

        return getById(id);
    }

    private static void setValueForPSWithoutID(Apartment apartment, PreparedStatement ps) throws SQLException {
        ps.setString(COLUMN_INDEX_ID, apartment.getAddress());
        ps.setInt(COLUMN_INDEX_CAPACITY, apartment.getCapacity());
        ps.setFloat(COLUMN_INDEX_PRICE, apartment.getPrice());
        ps.setLong(COLUMN_INDEX_REGION, apartment.getRegion().getRegionId());
    }

    @Override
    public Apartment getById(Long id) {
        try{
            Connection conn = dbConnector.getConnection();
            PreparedStatement ps = conn.prepareStatement(Apartment.SELECT_APARTMENT_BY_ID);
            ps.setLong(COLUMN_INDEX_ID, id);
            ResultSet rs = ps.executeQuery();
            Apartment apartment = new Apartment();

            if (rs.next()) {
                apartment = getApartment(rs);
            }else {
                throw new SQLException("Apartment not found. Id is null");
            }
            log.info("Apartment found with id {}", id);

            return apartment;
        }catch (SQLException e){
            log.error(ExceptionUtils.getStackTrace(e));
        }
        finally {
            dbConnector.closeConnection();
        }
        return null;
    }

    private Apartment getApartment(ResultSet rs) throws SQLException {

        Long regionID = rs.getLong(Apartment.REGION_REF);

        Region region = regionDao.getById(regionID);

        return new Apartment(
                rs.getLong(Apartment.ID),
                region,
                rs.getString(Apartment.ADDRESS),
                rs.getInt(Apartment.CAPACITY),
                rs.getFloat(Apartment.PRICE));
    }

    @Override
    public List<Apartment> getAll() {

        List<Apartment> apartments = new ArrayList<>();
        try{
            Connection conn = dbConnector.getConnection();
            PreparedStatement ps = conn.prepareStatement(Apartment.SELECT_APARTMENT);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Apartment apartment = getApartment(rs);
                apartments.add(apartment);
            }

            log.info("Show {} of apartments", rs.getRow());
        }catch (SQLException e){
            log.error(ExceptionUtils.getStackTrace(e));
        }
        finally {
            dbConnector.closeConnection();
        }
        return apartments;
    }

    @Override
    public void delete(Long id) {
        try{
            Connection conn = dbConnector.getConnection();
            PreparedStatement ps = conn.prepareStatement(Apartment.DELETE_APARTMENT);
            ps.setLong(1, id);
            ps.executeUpdate();

            log.info("Apartment with id {} deleted successfully", id);
        }catch (SQLException e){
            log.error(ExceptionUtils.getStackTrace(e));
        }
        finally {
            dbConnector.closeConnection();
        }
    }

    public void createTable(){
        try(Connection conn = dbConnector.getConnection();
            Statement stmt = conn.createStatement()){
            stmt.execute(Apartment.DROP_TABLE);
            stmt.execute(Region.DROP_TABLE);
            stmt.execute(Region.CREATE_TABLE);
            stmt.execute(Apartment.CREATE_TABLE);
        } catch (SQLException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }finally {
            dbConnector.closeConnection();
        }
    }

    public List<Apartment> selectByAddress(String address){

        List<Apartment> apartments = new ArrayList<>();
        try{
            Connection conn = dbConnector.getConnection();
            PreparedStatement ps = conn.prepareStatement(Apartment.SELECT_APARTMENT_BY_ADDRESS);
            ps.setString(PARAMETER_INDEX_ADDRESS_BY_ADDRESS, address);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Apartment apartment = getApartment(rs);
                apartments.add(apartment);
                log.info("Apartment with id {} deleted successfully", apartment.getApartmentId().toString());
            }


        }catch (SQLException e){
            log.error(ExceptionUtils.getStackTrace(e));
        }
        finally {
            dbConnector.closeConnection();
        }
        return apartments;
    }

    public List<Apartment> selectByRegion(Region region){

        List<Apartment> apartments = new ArrayList<>();
        try{
            Connection conn = dbConnector.getConnection();
            PreparedStatement ps = conn.prepareStatement(Apartment.SELECT_APARTMENT_BY_ADDRESS);
            ps.setLong(PARAMETER_INDEX_REGION_REF, region.getRegionId());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Apartment apartment = getApartment(rs);
                apartments.add(apartment);
                log.info("Apartment with id {} deleted successfully", apartment.getApartmentId().toString());
            }


        }catch (SQLException e){
            log.error(ExceptionUtils.getStackTrace(e));
        }
        finally {
            dbConnector.closeConnection();
        }
        return apartments;
    }

    public List<Apartment> selectByRegionAndAddress(Region region, String address){

        List<Apartment> apartments = new ArrayList<>();
        try{
            Connection conn = dbConnector.getConnection();
            PreparedStatement ps = conn.prepareStatement(Apartment.SELECT_APARTMENT_BY_REGION_AND_ADDRESS);
            ps.setLong(PARAMETER_INDEX_REGION_REF, region.getRegionId());
            ps.setString(PARAMETER_INDEX_ADDRESS_BY_ADDRESS_REGION, address);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Apartment apartment = getApartment(rs);
                apartments.add(apartment);
                log.info("Apartment with id {} deleted successfully", apartment.getApartmentId().toString());
            }


        }catch (SQLException e){
            log.error(ExceptionUtils.getStackTrace(e));
        }
        finally {
            dbConnector.closeConnection();
        }
        return apartments;
    }

    public List<Apartment> selectByRegionPriceBetween(Region region, float startPrice, float endPrice) {
        List<Apartment> apartments = new ArrayList<>();
        try{
            Connection conn = dbConnector.getConnection();
            PreparedStatement ps = conn.prepareStatement(Apartment.SELECT_APARTMENT_BY_REGION_AND_PRICE_BETWEEN);
            ps.setLong(PARAMETER_INDEX_REGION_REF, region.getRegionId());
            ps.setFloat(PARAMETER_INDEX_PRICE_START_BY_REGION, startPrice);
            ps.setFloat(PARAMETER_INDEX_PRICE_END_BY_REGION, endPrice);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Apartment apartment = getApartment(rs);
                apartments.add(apartment);
                log.info("Apartment with id {} deleted successfully", apartment.getApartmentId().toString());
            }


        }catch (SQLException e){
            log.error(ExceptionUtils.getStackTrace(e));
        }
        finally {
            dbConnector.closeConnection();
        }
        return apartments;
    }

    public List<Apartment> selectByCapacityBetweenPriceBetween(int startCapacity, int endCapacity,
                                                               float startPrice, float endPrice) {
        List<Apartment> apartments = new ArrayList<>();
        try{
            Connection conn = dbConnector.getConnection();
            PreparedStatement ps = conn.prepareStatement(Apartment.SELECT_APARTMENT_BY_CAPACITY_BETWEEN_PRICE_BETWEEN);
            ps.setInt(PARAMETER_INDEX_CAPACITY_START_BY_CAPACITY_PRICE, startCapacity);
            ps.setInt(PARAMETER_INDEX_CAPACITY_END_BY_CAPACITY_PRICE, endCapacity);
            ps.setFloat(PARAMETER_INDEX_PRICE_START_BY_CAPACITY_PRICE, startPrice);
            ps.setFloat(PARAMETER_INDEX_PRICE_END_BY_CAPACITY_PRICE, endPrice);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Apartment apartment = getApartment(rs);
                apartments.add(apartment);
                log.info("Apartment with id {} deleted successfully", apartment.getApartmentId().toString());
            }


        }catch (SQLException e){
            log.error(ExceptionUtils.getStackTrace(e));
        }
        finally {
            dbConnector.closeConnection();
        }
        return apartments;
    }

    public List<Apartment> selectByRegionCapacityBetweenPriceBetween(Region region, int startCapacity, int endCapacity,
                                                                     float startPrice, float endPrice) {
        List<Apartment> apartments = new ArrayList<>();
        try{
            Connection conn = dbConnector.getConnection();
            PreparedStatement ps = conn.prepareStatement(Apartment.SELECT_APARTMENT_BY_REGION_CAPACITY_BETWEEN_PRICE_BETWEEN);

            ps.setLong(PARAMETER_INDEX_REGION_REGION_BY_CAPACITY_PRICE, region.getRegionId());
            ps.setInt(PARAMETER_INDEX_CAPACITY_START_BY_REGION_CAPACITY_PRICE, startCapacity);
            ps.setInt(PARAMETER_INDEX_CAPACITY_END_BY_REGION_CAPACITY_PRICE, endCapacity);
            ps.setFloat(PARAMETER_INDEX_PRICE_START_BY_REGION_CAPACITY_PRICE, startPrice);
            ps.setFloat(PARAMETER_INDEX_PRICE_END_BY_REGION_CAPACITY_PRICE, endPrice);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Apartment apartment = getApartment(rs);
                apartments.add(apartment);
                log.info("Apartment with id {} deleted successfully", apartment.getApartmentId().toString());
            }


        }catch (SQLException e){
            log.error(ExceptionUtils.getStackTrace(e));
        }
        finally {
            dbConnector.closeConnection();
        }
        return apartments;
    }
}
