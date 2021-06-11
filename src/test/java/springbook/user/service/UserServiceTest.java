package springbook.user.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static springbook.user.service.UserService.MIN_LOGCOUNT_FOR_SILVER;
import static springbook.user.service.UserService.MIN_RECCOMEND_FOR_GOLD;

import java.util.Arrays;
import java.util.List;
import javax.sql.DataSource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/test-applicationContext.xml")
public class UserServiceTest {

    @Autowired
    UserService userService;
    @Autowired
    UserDao userDao;
    @Autowired
    DataSource dataSource;
    @Autowired
    PlatformTransactionManager transactionManager;
    List<User> users; // 텍스트 픽스처

    @Before
    public void setUp() {
        users = Arrays.asList(
            new User("bumjin", "박범진", "p1", "bumjin@naver.com" ,Level.BASIC, MIN_LOGCOUNT_FOR_SILVER - 1, 0),
            new User("joytouch", "강명성", "p2", "joytouch@naver.com",Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0),
            new User("erwins", "신승한", "p3", "erwins@naver.com",Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD - 1),
            new User("madnite1", "이상호", "p4","madnite1@naver.com" ,Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD),
            new User("green", "오민규", "p5","green@naver.com" ,Level.GOLD, 100, Integer.MAX_VALUE)
        );
    }

    // 각 사용자별로 업그레이드 후의 예상 레벨을 검증한다.
    @Test
    public void upgradeLevels() throws Exception {
        userDao.deleteAll();
        for (User user : users) {
            userDao.add(user);
        }
        userService.upgradeLevels();
        checkLevelUpgraded(users.get(0), false);
        checkLevelUpgraded(users.get(1), true);
        checkLevelUpgraded(users.get(2), false);
        checkLevelUpgraded(users.get(3), true);
        checkLevelUpgraded(users.get(4), false);
    }

    // 어떤 레벨로 바뀔 것인가가 아니라, 다음 레벨로 업 그레이드될 것인가 아닌가를 체크한다.
    private void checkLevelUpgraded(User user, boolean upgraded) {
        User userUpdate = userDao.get(user.getId());
        if (upgraded) { // 업그레이드가 일어났는지 확인
            assertThat(userUpdate.getLevel(),
                is(user.getLevel().nextLevel())); // 다음 레벨이 무엇인지는 Level에게 물어보면 된다.
        } else { // 업그레이드가 일어나지 않았는지 확인
            assertThat(userUpdate.getLevel(), is(user.getLevel()));
        }
    }

    @Test
    public void add() {
        userDao.deleteAll();

        User userWithLevel = users.get(4); // GOLD 레벨(GOLD 레벨이 이미 지정된 User라면 레벨을 초기화하지 않아야 한다.)
        User userWithoutLevel = users.get(0); // 레벨이 비어 있는 사용자. 로직에 따라 등록 중에 BASIC 레벨도 설정돼야 한다.
        userWithoutLevel.setLevel(null);

        userService.add(userWithLevel);
        userService.add(userWithoutLevel);

        // DB에 저장된 결과를 가져와 확인한다.
        User userWithLevelRead = userDao.get(userWithLevel.getId());
        User userWithoutLevelRead = userDao.get(userWithoutLevel.getId());

        assertThat(userWithLevelRead.getLevel(), is(userWithLevel.getLevel()));
        assertThat(userWithoutLevelRead.getLevel(), is(Level.BASIC));
    }


    @Test
    public void upgradeAllOrNothing() throws Exception {
        // 예외를 발생시킬 네 번째 사용자의 id를 넣어서 테스트용 UserService 대역 오브젝트를 생성한다.
        UserService testUserService = new TestUserService(users.get(3).getId());
        testUserService.setUserDao(this.userDao);
        testUserService.setTransactionManager(transactionManager);

        userDao.deleteAll();
        for (User user : users) {
            userDao.add(user);
        }
        try { // TestUserService는 업그레이드 작업 중에 예외가 발생해야 한다. 정상 종료라면 문제가 있으니 실패
            testUserService.upgradeLevels();
            fail("TestUserServiceException expected");
        } catch (TestUserServiceException e) { // TestUserService가 던져주는 예외를 잡아서 계속 진행되도록 한다. 그 외의 예외라면 테스트 실패
        }
        checkLevelUpgraded(users.get(1), false); // 예외가 발생하기 전에 레벨 변경이 있었던 사용자의 레벨이 처음 상태로 바뀌었나 확인
    }

    static class TestUserService extends UserService {

        private String id;

        private TestUserService(String id) { // 예외를 발생시킬 User 오브젝트의 id를 지정할 수 있게 만든다.
            this.id = id;
        }

        protected void upgradeLevel(User user) { // UserService의 메소드를 오버라이드한다.
            if (user.getId().equals(this.id)) { // 지정된 id의 User 오브젝트가 발견되면 예외를 던져서 작업을 강제로 중단시킨다.
                throw new TestUserServiceException();
            }
            super.upgradeLevel(user);
        }
    }

    static class TestUserServiceException extends RuntimeException {

    }

}