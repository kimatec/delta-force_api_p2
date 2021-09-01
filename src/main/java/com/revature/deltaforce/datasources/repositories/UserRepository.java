package com.revature.deltaforce.datasources.repositories;


import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.revature.deltaforce.datasources.models.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepository implements CrudRepository<AppUser> {

    private final MongoCollection<AppUser> usersCollection;

    @Autowired
    public UserRepository(MongoClient mongoClient) {
        this.usersCollection = mongoClient.getDatabase("DeltaForce").getCollection("users", AppUser.class);
    }

    @Override
    public List<AppUser> findAll() {
        return null;
    }

    @Override
    public AppUser findById(String id) {
        return null;
    }

    @Override
    public AppUser save(AppUser newResource) {
        return null;
    }

    @Override
    public boolean update(AppUser updatedResource) {
        return false;
    }

    @Override
    public boolean deleteById(int id) {
        return false;
    }
}