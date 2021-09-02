package com.revature.deltaforce.datasources.repositories;


import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.revature.deltaforce.datasources.models.AppUser;
import com.revature.deltaforce.util.exceptions.DataSourceException;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.bson.Document;

import javax.print.Doc;
import java.util.List;

@Repository
public class UserRepository implements CrudRepository<AppUser> {

    private final MongoCollection<AppUser> usersCollection;

    @Autowired
    public UserRepository(MongoClient mongoClient) {
        this.usersCollection = mongoClient.getDatabase("DeltaForce").getCollection("users", AppUser.class);
    }

    public AppUser findUserByCredentials(String username, String encryptedPass) {

        try{

            Document queryDoc = new Document("username", username).append("password", encryptedPass);
            return usersCollection.find(queryDoc).first();

        }catch (Exception e){
            throw new DataSourceException("An unexpected exception");
        }

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

        try {
            newResource.setId(new ObjectId().toString());
            usersCollection.insertOne(newResource);
            return newResource;
        } catch(Exception e) {
            throw new DataSourceException(e.getMessage());
        }

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