package net.vrakin.dao;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.vrakin.dto.Apartment;
import net.vrakin.dto.Region;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@NoArgsConstructor
public class ApartmentDao implements Dao<Apartment> {

    public final DBConnector dbConnector = new DBConnector();

    private final RegionDao regionDao = new RegionDao();

    @Override
    public Apartment save(Apartment apartment) {

        String sqlQuery = null;
        Apartment resultApartment = null;

        if (apartment.getApartment_id() == null) {
            sqlQuery = Apartment.INSERT_APARTMENT;
        }else{
            sqlQuery = Apartment.UPDATE_APARTMENT;
        }

        try(Connection conn = dbConnector.getConnection();
            PreparedStatement ps = conn.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS)){

            ps.setString(1, apartment.getAddress());
            ps.setInt(2, apartment.getCapacity());
            ps.setFloat(3, apartment.getPrice());
            ps.setLong(4, apartment.getRegion().getRegionId());

            ps.executeUpdate();

            if (apartment.getApartment_id() != null){
                log.info("Apartment {} update successfully", apartment);
                resultApartment = apartment;
            }else {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    resultApartment = new Apartment();
                    resultApartment.setApartment_id(rs.getLong(1));
                    resultApartment.setAddress(apartment.getAddress());
                    resultApartment.setCapacity(apartment.getCapacity());
                    resultApartment.setPrice(apartment.getPrice());
                    resultApartment.setRegion(apartment.getRegion());
                    log.info("Apartment {} saved successfully", apartment);
                } else {
                    log.info("Apartment save failed");
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            dbConnector.closeConnection();
        }

        return resultApartment;
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
            Apartment apartment = getApartment(rs);

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

        Long regionID = rs.getLong("region_id");

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
        try{
            Connection conn = dbConnector.getConnection();
            PreparedStatement ps = conn.prepareStatement(Apartment.SELECT_APARTMENT);
            ResultSet rs = ps.executeQuery();

            List<Apartment> apartments = new ArrayList<>();
            while(rs.next()) {
                Apartment apartment = getApartment(rs);
                apartments.add(apartment);
            }

            log.info("Show {} of apartments", rs.getRow());

            return apartments;
        }catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            dbConnector.closeConnection();
        }
        return null;
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
