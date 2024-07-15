package net.vrakin.dao;

import lombok.extern.slf4j.Slf4j;
import net.vrakin.dto.Apartment;
import net.vrakin.dto.Region;
import org.junit.Assert;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;

@Slf4j
public class ApartmentDaoTest {

    private final static ApartmentDao apartmentDao = new ApartmentDao();
    private final static RegionDao regionDao = new RegionDao();

    static{
            apartmentDao.createTable();
    }



    @Test
    public void save() {
        Apartment apt = new Apartment();
        apt.setAddress("123 Main St");
        apt.setPrice(25000F);
        apt.setCapacity(2);

        List<Region> regions = regionDao.getAll();

        Region region = null;

        if (regions.isEmpty()) {
            region = new Region();
            region.setName("TestRegion");
            region = regionDao.save(region);
        }else {
            region = regions.get(0);
        }

        log.info(region.toString());
        apt.setRegion(region);

        Apartment new_apt = apartmentDao.save(apt);

        Assert.assertNotNull(new_apt);
        Assert.assertEquals(apt.getAddress(), new_apt.getAddress());
        Assert.assertEquals(apt.getPrice(), new_apt.getPrice());
        Assert.assertEquals(apt.getCapacity(), new_apt.getCapacity());
        Assert.assertEquals(apt.getRegion(), new_apt.getRegion());
    }

    @Test
    public void getById() {
    }

    @Test
    public void getAll() {
    }

    @Test
    public void delete() {
    }
}