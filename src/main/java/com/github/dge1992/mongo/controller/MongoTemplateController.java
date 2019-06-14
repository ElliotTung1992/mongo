package com.github.dge1992.mongo.controller;

import com.github.dge1992.mongo.doamin.User;
import com.github.dge1992.mongo.doamin.UserParam;
import com.mongodb.WriteResult;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.regex.Pattern;

/**
 * @Author dongganene
 * @Description
 * @Date 2019/6/14
 **/
@RequestMapping("/userTwo")
@RestController
public class MongoTemplateController {

    @Autowired
    private MongoTemplate mongoTemplate;

    @RequestMapping("/insert")
    public Object insert(@RequestBody User user){
        mongoTemplate.insert(user);
        return "success";
    }

    @RequestMapping("/insertTwo")
    public Object insertTwo(@RequestBody User user){
        mongoTemplate.insert(user, "userTwo");
        return "success";
    }

    @RequestMapping("/insertList")
    public Object insertList(@RequestBody UserParam userParam){
        List<User> users = userParam.getUsers();
        mongoTemplate.insert(users, "user");
        return "success";
    }

    @RequestMapping("/findByPhoneLike")
    public Object findByPhoneLike(){
        Pattern pattern = Pattern.compile("^.*3$",Pattern.CASE_INSENSITIVE);
        Query query = new Query(Criteria.where("phone").regex(pattern));
        List<User> users = mongoTemplate.find(query,User.class);
        return users;
    }

    @RequestMapping("/findByNameAndAge")
    public Object findByNameAndAge(){
        Query query = new Query();
        query.addCriteria(Criteria.where("name").is("ljj"));
        query.addCriteria(Criteria.where("age").is(20));
        List<User> users = mongoTemplate.find(query, User.class,"user");
        return users;
    }

    @RequestMapping("/findByNameAndAge2")
    public Object findByNameAndAge2(){
        Criteria criteria = new Criteria();
        criteria.and("name").is("ljj");
        criteria.and("age").is(20);
        Query query = new Query(criteria);
        List<User> users = mongoTemplate.find(query, User.class,"user");
        return users;
    }

    @RequestMapping("/updateById")
    public Object updateById(){
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(1));
        Update update = Update.update("name", "dge1992");
        UpdateResult result = mongoTemplate.updateMulti(query, update, "user");
        return result.getMatchedCount();
    }

    /**
     * @author dongganen
     * @date 2019/6/14
     * @desc: 修改文档中的数组
     */
    @RequestMapping("/updateDogsById")
    public Object updateDogsById(){
        Query query = Query.query(Criteria.where("_id").is("4").and("dogs._id").is(1));
        Update update1 = new Update();
        Update update = Update.update("dogs.1.varieties", "比特111");
        UpdateResult userList = mongoTemplate.upsert(query, update, "user");
        return userList.getMatchedCount();
//        return mongoTemplate.find(query, User.class, "user");
    }
}
