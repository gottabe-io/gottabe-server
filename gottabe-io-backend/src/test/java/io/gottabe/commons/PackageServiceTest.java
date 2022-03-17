package io.gottabe.commons;

import io.gottabe.commons.entities.PackageGroup;
import io.gottabe.commons.repositories.PackageGroupRepository;
import io.gottabe.commons.services.PackageGroupService;
import io.gottabe.game.GottabeServerApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = GottabeServerApplication.class)
public class PackageServiceTest {

    @MockBean
    private PackageGroupRepository packageGroupRepository;

    @Autowired
    private PackageGroupService packageGroupService;

    @Test
    public void testNewMatch() {
        when(packageGroupRepository.save(any())).thenAnswer(a -> a.getArguments()[0]);
        packageGroupService.save(PackageGroup.builder().build());
    }

}
