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
public class RegionDao implements Dao<Region> {

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
                ps.setString(1, region.getName());
            }else{
                sqlQuery = Region.UPDATE_REGION;
                ps = conn.prepareStatement(sqlQuery);
                ps.setString(1, region.getName());
                ps.setInt(2, Math.toIntExact(region.getRegionId()));
            }

            long resultQuery = ps.executeUpdate();
            log.info("Result execute update: {}", resultQuery);

            if (region.getRegionId() == null) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    long id = rs.getLong(1);
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
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            Region region = getRegion(rs);

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
        if (rs.next()) {
            Region region = new Region(rs.getLong(Region.ID),
                    rs.getString(Region.NAME));
            return region;
        }else {
            throw new SQLException("Region not found");
        }
    }

    @Override
    public List<Region> getAll() {

        List<Region> regions = new ArrayList<>();
        try{
            Connection conn = dbConnector.getConnection();
            PreparedStatement ps = conn.prepareStatement(Region.SELECT_REGION);
            ResultSet rs = ps.executeQuery();

            if (rs.getRow()>0) {
                while (rs.next()) {
                    Region region = getRegion(rs);
                    regions.add(region);
                }
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
            ps.setLong(1, id);
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
}
