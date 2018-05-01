package com.radzkov.resource.service.notifications;

import com.radzkov.resource.entity.ClothesItem;
import com.radzkov.resource.entity.ClothesType;
import com.radzkov.resource.repository.ClothesItemRepository;
import com.radzkov.resource.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Radzkov Andrey
 */
@RunWith(MockitoJUnitRunner.class)
public class DirtyClothesNotificationGroupingServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private ClothesItemRepository clothesItemRepository;

    @InjectMocks
    private DirtyClothesNotificationGroupingService groupingService;

    @Test
    public void countOfRestCleanClothesByType() {
        List<ClothesItem> sendersCleanClothes = new ArrayList<>();
        sendersCleanClothes.add(getClothesItem("t-shirt"));
        sendersCleanClothes.add(getClothesItem("t-shirt"));
        sendersCleanClothes.add(getClothesItem("t-shirt"));
        sendersCleanClothes.add(getClothesItem("socks"));
        sendersCleanClothes.add(getClothesItem("trousers"));
        sendersCleanClothes.add(getClothesItem("trousers"));

        Map<ClothesType, Long> clothesTypes = groupingService.countOfClothesByType(sendersCleanClothes);

        ClothesType tShirt = getClothesType("t-shirt");
        ClothesType socks = getClothesType("socks");
        ClothesType trousers = getClothesType("trousers");
        assertThat(clothesTypes.get(tShirt), is(3L));
        assertThat(clothesTypes.get(socks), is(1L));
        assertThat(clothesTypes.get(trousers), is(2L));

    }

    private ClothesItem getClothesItem(String typeName) {
        ClothesItem clothesItem = new ClothesItem();
        ClothesType type = getClothesType(typeName);
        clothesItem.setType(type);
        return clothesItem;
    }

    private ClothesType getClothesType(String name) {
        ClothesType type = new ClothesType();
        type.setName(name);
        return type;
    }
}
