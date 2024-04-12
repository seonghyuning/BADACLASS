package com.ocean.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ocean.model.entity.Oneday;
import com.ocean.model.entity.User;
import com.ocean.model.entity.WishList;
import com.ocean.repository.WishListRepository;

@Service
public class WishListService {

	@Autowired
    private WishListRepository wishListRepository;
	
	
	// 특정 사용자의 위시리스트 가져오기
    public List<WishList> getWishListByUser(int userid) {
        return wishListRepository.findByUserId(userid);
    }
	
    //찜하기 하면 상태 업데이트
    public void updateWishListStatus(int onedayId, int userId, boolean inWishlist) {
        // WishList 엔티티를 찾거나 생성합니다.
        WishList wishList = wishListRepository.findByUserIdAndOnedayId(userId, onedayId)
                .orElseGet(() -> {
                    Oneday oneday = new Oneday();
                    oneday.setId(onedayId);
                    
                    User user = new User();
                    user.setId(userId);

                    WishList newWishList = new WishList();
                    newWishList.setUser(user);
                    newWishList.setOneday(oneday);
                    return newWishList;
                });

        // WishList의 상태를 업데이트합니다.
        wishList.setInWishlist(inWishlist);

        // WishList를 저장합니다.
        wishListRepository.save(wishList);
    }

    public boolean isOnWishlist(User user, Oneday oneday) {
        // WishList 엔티티에서 사용자와 원데이를 기반으로 정보를 조회
        WishList wishList = wishListRepository.findByUserAndOneday(user, oneday);
        
        // 조회된 정보가 null이면 찜되지 않은 상태
        return wishList != (null) && wishList.isInWishlist();
    }
    // 위시리스트에서 아이템 삭제
    public void deleteWishlistItem(int onedayId, int userId) {
        wishListRepository.findByUserIdAndOnedayId(userId, onedayId)
                .ifPresent(wishListRepository::delete);
    }
}
