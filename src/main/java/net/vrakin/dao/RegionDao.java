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
public class RegionDao implements Dao<Region> {

    public static final int COLUMN_INDEX_REGION_ID = 1;
    public static final int COLUMN_INDEX_NAME_INSERT = 1;


    public static final int EMPTY_VALUE = 0;
    public static final int COLUMN_INDEX_NAME_UPDATE = 1;
    public static final int COLUMN_INDEX_ID_UPDATE = 2;
    public static final int PARAMETER_INDEX_NAME_BY_NAME = 1;
    public final DBConnector dbConnector = new DBConnector();

    @Override
    public Region add(Region region) {

        Region resultRegion = region;
        try{
            Connection conn = dbConnector.getConnection();
            String sqlQuery = null;
            PreparedStatement ps = null;

            if (region.getRegionId() == null) {
                sqlQuery = Region.INSERT_REGION;
                ps = conn.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
                ps.setString(COLUMN_INDEX_NAME_INSERT, region.getName());
            }else{
                sqlQuery = Region.UPDATE_REGION;
                ps = conn.prepareStatement(sqlQuery);
                ps.setString(COLUMN_INDEX_NAME_UPDATE, region.getName());
                ps.setInt(COLUMN_INDEX_ID_UPDATE, Math.toIntExact(region.getRegionId()));
            }

            long resultQuery = ps.executeUpdate();
            log.info("Result execute update: {}", resultQuery);

            if (region.getRegionId() == null) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    long id = rs.getLong("GENERATED_KEY");
                    resultRegion.setRegionId(id);
                }
                log.info("Region saved successfully");
            }else {
                log.info("Region save failed");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            dbConnector.closeConnection();
        }
        return resultRegion;
    }

    @Override
    public Apartment update(Apartment apartment) {
        return null;
    }

    @Override
    public Region getById(Long id) {
        try{
            Connection conn = dbConnector.getConnection();
            PreparedStatement ps = conn.prepareStatement(Region.SELECT_REGION_BY_ID);
            ps.setLong(COLUMN_INDEX_REGION_ID, id);
            ResultSet rs = ps.executeQuery();
            Region region = null;
            if (rs.next()) {
                region = getRegion(rs);
            }

            log.info("Region found with id {}", id);

            return region;
        }catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            dbConnector.closeConnection();
        }
        return null;
    }

    private static Region getRegion(ResultSet rs) throws SQLException {
        Region region = new Region(rs.getLong(COLUMN_INDEX_REGION_ID),
                rs.getString(Region.NAME));
        return region;
    }

    @Override
    public List<Region> getAll() {

        List<Region> regions = new ArrayList<>();
        try{
            Connection conn = dbConnector.getConnection();
            PreparedStatement ps = conn.prepareStatement(Region.SELECT_REGION);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Region region = getRegion(rs);
                regions.add(region);
            }

            log.info("Show {} of regions", rs.getRow());
        }catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            dbConnector.closeConnection();
        }
        return regions;
    }

    @Override
    public void delete(Long id) {
        try{
            Connection conn = dbConnector.getConnection();
            PreparedStatement ps = conn.prepareStatement(Region.DELETE_REGION);
            ps.setLong(COLUMN_INDEX_REGION_ID, id);
            ps.executeUpdate();

            log.info("Region with id {} deleted successfully", id);
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
            stmt.execute(Region.DROP_TABLE);
            stmt.execute(Region.CREATE_TABLE);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }finally {
            dbConnector.closeConnection();
        }
    }

    public List<Region> selectByName(String name){

        List<Region> regions = new ArrayList<>();
        try{
            Connection conn = dbConnector.getConnection();
            PreparedStatement ps = conn.prepareStatement(Region.SELECT_REGION_BY_NAME);
            ps.setString(PARAMETER_INDEX_NAME_BY_NAME, name);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Region region = getRegion(rs);
                regions.add(region);
                log.info("Apartment with id {} deleted successfully", region.getRegionId().toString());
            }


        }catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            dbConnector.closeConnection();
        }
        return regions;
    }

}
