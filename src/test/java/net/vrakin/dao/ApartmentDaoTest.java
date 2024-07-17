package net.vrakin.dao;

import lombok.extern.slf4j.Slf4j;
import net.vrakin.dto.Apartment;
import net.vrakin.dto.Region;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

@Slf4j
public class ApartmentDaoTest {

    private final static ApartmentDao apartmentDao = new ApartmentDao();
    private final static RegionDao regionDao = new RegionDao();
    public static final int TEST_SHIFT_CAPACITY = 1;

    static{
            apartmentDao.createTable();
    }


    @Test
    public void add() {
        Apartment apt = getApartment();

        Apartment new_apt = apartmentDao.add(apt);

        Assert.assertNotNull(new_apt);
        Assert.assertEquals(apt.getAddress(), new_apt.getAddress());
        Assert.assertEquals(apt.getPrice(), new_apt.getPrice());
        Assert.assertEquals(apt.getCapacity(), new_apt.getCapacity());
        Assert.assertEquals(apt.getRegion().getRegionId().longValue(), new_apt.getRegion().getRegionId().longValue());
        Assert.assertEquals(apt.getRegion().getName(), new_apt.getRegion().getName());
    }

    @Test
    public void getById() {
        Apartment apt = getApartment();

        Apartment new_apt = apartmentDao.add(apt);

        Long id = new_apt.getApartmentId();

        Apartment test_apt = apartmentDao.getById(id);

        Assert.assertNotNull(test_apt);
        Assert.assertEquals(apt.getAddress(), test_apt.getAddress());
        Assert.assertEquals(apt.getPrice(), test_apt.getPrice());
        Assert.assertEquals(apt.getCapacity(), test_apt.getCapacity());
        Assert.assertEquals(apt.getRegion().getRegionId().longValue(), test_apt.getRegion().getRegionId().longValue());
        Assert.assertEquals(apt.getRegion().getName(), test_apt.getRegion().getName());
    }

    private static Apartment getApartment() {
        Apartment apt = new Apartment();
        apt.setAddress("123 Main St");
        apt.setPrice(25000F);
        apt.setCapacity(2);
        Region region = null;

        List<Region> regions = regionDao.getAll();

        if (regions.isEmpty()) {
            region = new Region();
            region.setName("TestRegion");
            region = regionDao.add(region);
        }else {
            region = regions.get(0);
        }

        log.info(region.toString());
        apt.setRegion(region);
        return apt;
    }

    @Test
    public void getAll() {
        int countAll = apartmentDao.getAll().size();
        log.info("countAll: {}", countAll);
        apartmentDao.add(getApartment());
        int countAll2 = apartmentDao.getAll().size();
        Assert.assertEquals(countAll + 1, countAll2);
    }

    @Test
    public void delete() {
        int countAll = apartmentDao.getAll().size();
        log.info("countAll: {}", countAll);
        Apartment apt = apartmentDao.add(getApartment());
        apartmentDao.delete(apt.getApartmentId());
        Assert.assertEquals(countAll, apartmentDao.getAll().size());
    }

    @Test
    public void update(){
        List<Apartment> apartments = apartmentDao.getAll();

        Apartment apt;

        if(!apartments.isEmpty()){
            apt = apartments.stream().findFirst().get();
        }else{
            apt = apartmentDao.add(getApartment());
        }

        Integer testCapacity = apt.getCapacity() + TEST_SHIFT_CAPACITY;
        apt.setCapacity(testCapacity);

        apartmentDao.update(apt);

        Apartment test_apt = apartmentDao.getById(apt.getApartmentId());
        Assert.assertNotNull(test_apt);
        Assert.assertEquals(apt.getCapacity(), testCapacity);
    }
}