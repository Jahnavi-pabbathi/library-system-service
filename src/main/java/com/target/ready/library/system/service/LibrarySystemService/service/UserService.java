package com.target.ready.library.system.service.LibrarySystemService.service;

import com.target.ready.library.system.service.LibrarySystemService.entity.Book;
import com.target.ready.library.system.service.LibrarySystemService.entity.UserCatalog;
import com.target.ready.library.system.service.LibrarySystemService.entity.UserProfile;
import com.target.ready.library.system.service.LibrarySystemService.exceptions.ResourceAlreadyExistsException;
import com.target.ready.library.system.service.LibrarySystemService.exceptions.ResourceNotFoundException;
import com.target.ready.library.system.service.LibrarySystemService.repository.BookRepository;
import com.target.ready.library.system.service.LibrarySystemService.repository.UserCatalogRepository;
import com.target.ready.library.system.service.LibrarySystemService.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final UserCatalogRepository userCatalogRepository;
    private final UserRepository userRepository;

    private final BookRepository bookRepository;

    @Autowired
    public UserService(UserCatalogRepository userCatalogRepository, UserRepository userRepository,BookRepository bookRepository) {
        this.userCatalogRepository = userCatalogRepository;
        this.userRepository = userRepository;
        this.bookRepository=bookRepository;
    }



    public List<UserCatalog> findBooksByUserId(int userId) {
        List<UserCatalog> userCatalogs = userCatalogRepository.findByUserId(userId);
        System.out.println(userCatalogs);
        if(userCatalogs.isEmpty()){
            throw new ResourceNotFoundException("No book is issued to this user");
        }
        return userCatalogs;
    }

    public Integer deleteBookByUserId(int userId, int bookId) {
        UserProfile userProfile=userRepository.findByUserId(userId);
        if(userProfile==null){
            throw new ResourceNotFoundException("User doesn't exist");
        }
        Book book=bookRepository.findById(bookId).orElse(null);
        if(book==null){
            throw new ResourceNotFoundException("Book doesn't exist");
        }
        UserCatalog userCatalog=userCatalogRepository.findByBookIdAndUserId(bookId,userId);
        if(userCatalog==null){
            throw new ResourceNotFoundException("User doesn't have this book");
        }
        return userCatalogRepository.deleteByBookIdAndUserId(bookId, userId);

    }

    public UserCatalog addUserCatalog(UserCatalog userCatalog) {
        try {
            UserProfile userProfile = userRepository.findByUserId(userCatalog.getUserId());
            if (userProfile == null) {
                throw new ResourceNotFoundException("Student does not exists");
            }
            return userCatalogRepository.save(userCatalog);
        } catch (DataIntegrityViolationException e) {
            throw new ResourceAlreadyExistsException("The student already has the given book");
        }

    }

    public UserProfile addUser(UserProfile userProfile) {
        UserProfile userProfile1 = userRepository.findById(userProfile.getUserId()).orElse(null);
        if (userProfile1 == null) {
            return userRepository.save(userProfile);
        } else {
            throw new ResourceAlreadyExistsException("User already Exists");
        }

    }

    public int deleteUser(int userId) {
        if (userRepository.findByUserId(userId) == null) {
            throw new ResourceNotFoundException("User does not exist");
        }
        if (userCatalogRepository.findByUserId(userId).size() > 0) {
            throw new ResourceAlreadyExistsException("User has books so can't delete the user");
        }

        userRepository.deleteByUserId(userId);
        return userId;
    }


    public UserProfile findByUserId(int userId) {
        if (userRepository.findByUserId(userId) == null) {
            throw new ResourceNotFoundException("User does not exist");
        }
        return userRepository.findByUserId(userId);
    }

    public List<UserProfile> getAllUsers () {
        List<UserProfile> users = userRepository.findAll();
        if (users.isEmpty()) {
            throw new ResourceNotFoundException("Currently no users!");
        }
        return users;
    }
}

