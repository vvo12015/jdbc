package net.vrakin.dao;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.vrakin.dto.Apartment;
import net.vrakin.dto.Region;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@NoArgsConstructor
public class ApartmentDao implements Dao<Apartment> {

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
                id = rs.getLong(1);
            }
        }catch (SQLException e){
            e.printStackTrace();
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
            ps.setLong(5, apartment.getApartmentId());
            ps.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            dbConnector.closeConnection();
        }

        return getById(id);
    }

    private static void setValueForPSWithoutID(Apartment apartment, PreparedStatement ps) throws SQLException {
        ps.setString(1, apartment.getAddress());
        ps.setInt(2, apartment.getCapacity());
        ps.setFloat(3, apartment.getPrice());
        ps.setLong(4, apartment.getRegion().getRegionId());
    }

    private List<Apartment> getByAllFields(Apartment apartment) {

        StringBuilder sql = getSQL_Query(apartment);

        log.info(sql.toString());
        List<Apartment> apartments = new ArrayList<>();

        try{
            Connection conn = dbConnector.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql.toString());
            ResultSet rs = ps.executeQuery();


            while(rs.next()) {
                Apartment apartmentNext = getApartment(rs);
                apartments.add(apartmentNext);
            }

            log.info("Show {} apartment ", apartments.size());

            if(!apartments.isEmpty()){
                log.info("Show apartment by all fields first apartment{}", apartments.get(0).toString());
            };

        }catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            dbConnector.closeConnection();
        }
        return apartments;
    }

    private static StringBuilder getSQL_Query(Apartment apartment) {
        StringBuilder sql = new StringBuilder();
        sql.append(Apartment.SELECT_BY_ALL_PARAMETERS);

        if (apartment.getRegion() != null) {
            sql.append(String.format("%s = ?", Apartment.REGION_REF));
        }

        if (apartment.getAddress() != null) {
            if (apartment.getRegion() != null) {sql.append(" AND ");}
            sql.append(String.format("%s = ?", Apartment.ADDRESS));
        }

        if (apartment.getCapacity() != null) {
            if ((apartment.getRegion() != null) ||
                    (apartment.getAddress() != null)){sql.append(" AND ");}
            sql.append(String.format("%s = ? ", Apartment.CAPACITY));
        }

        if (apartment.getPrice() != null) {
            if ((apartment.getRegion() != null) ||
                    (apartment.getAddress() != null) ||
                    (apartment.getCapacity() != null) ){sql.append(" AND ");}
            sql.append(String.format("%s = ? ", Apartment.PRICE));
        }
        return sql;
    }

    @Override
    public Apartment getById(Long id) {
        try{
            Connection conn = dbConnector.getConnection();
            PreparedStatement ps = conn.prepareStatement(Apartment.SELECT_APARTMENT_BY_ID);
            ps.setLong(1, id);
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
            e.printStackTrace();
        }
        finally {
            dbConnector.closeConnection();
        }
        return null;
    }

    private Apartment getApartment(ResultSet rs) throws SQLException {

        Long regionID = rs.getLong(1);

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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
            throw new RuntimeException(e);
        }finally {
            dbConnector.closeConnection();
        }
    }


}
