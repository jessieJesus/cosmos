package com.practice.by.yevhen.app.repository;

import com.practice.by.yevhen.app.dto.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

}
