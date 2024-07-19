package net.vrakin.dao;

import lombok.extern.slf4j.Slf4j;
import net.vrakin.dto.Apartment;
import net.vrakin.dto.Region;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.stream.IntStream;

@Slf4j
public class SelectApartmentTest {

    private final static ApartmentDao apartmentDao = new ApartmentDao();
    private final static RegionDao regionDao = new RegionDao();
    public static final int START_TEST_NUMBER = 0;
    private static final int THREE_NUMBER = 3;
    private static final int TWO_NUMBER = 2;
    private static final int FIVE_NUMBER = 5;
    public static final String ADDRESS_NAME = "address";

    @Before
    public void setUp() throws Exception {
        apartmentDao.createTable();

        IntStream.of(TWO_NUMBER, THREE_NUMBER, FIVE_NUMBER)
                .forEach(i ->{
                            Region region = new Region("region" + i);
                            Region saveregion = regionDao.add(region);
                            log.info(saveregion.toString());
                            IntStream.range(START_TEST_NUMBER, i)
                                .forEach(j->{
                                            Apartment apartment = new Apartment(saveregion, ADDRESS_NAME + i,
                                                    i, i*1000.00F);
                                            apartmentDao.add(apartment);
                                            log.info(apartment.toString());
                                        });
                            });
    }

    @Test
    public void findByAddress() {
        Assert.assertEquals(apartmentDao.selectByAddress(ADDRESS_NAME + TWO_NUMBER).size(), TWO_NUMBER);
    }

    @Test
    public void findByRegionAddress() {
        String regionName = "region" + THREE_NUMBER;
        Region region = regionDao.selectByName(regionName).stream().findFirst().get();
        Assert.assertEquals(apartmentDao.selectByRegionAndAddress(region, ADDRESS_NAME + THREE_NUMBER).size(), THREE_NUMBER);
    }

    @Test
    public void findByRegionPriceBetween() {
        String regionName = "region" + FIVE_NUMBER;
        Region region = regionDao.selectByName(regionName).stream().findFirst().get();
        Assert.assertEquals(apartmentDao.selectByRegionPriceBetween(region, FIVE_NUMBER * 1000F - 500F,
                FIVE_NUMBER * 1000F + 500F).size(), FIVE_NUMBER);
    }

    @Test
    public void findByCapacityBetweenPriceBetween() {
      Assert.assertEquals(apartmentDao.selectByCapacityBetweenPriceBetween(TWO_NUMBER-1, TWO_NUMBER,
              TWO_NUMBER * 1000F - 500F, TWO_NUMBER * 1000F + 500F).size(), TWO_NUMBER);
    }

    @Test
    public void findByRegionCapacityBetweenPriceBetween() {
        String regionName = "region" + FIVE_NUMBER;
        Region region = regionDao.selectByName(regionName).stream().findFirst().get();
        Assert.assertEquals(apartmentDao.selectByRegionCapacityBetweenPriceBetween(region, FIVE_NUMBER-1, FIVE_NUMBER,
                FIVE_NUMBER * 1000F - 500F, FIVE_NUMBER * 1000F + 500F).size(), FIVE_NUMBER);
    }
}
