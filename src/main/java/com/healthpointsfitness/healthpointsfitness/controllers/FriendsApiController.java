package com.healthpointsfitness.healthpointsfitness.controllers;

import com.healthpointsfitness.healthpointsfitness.models.FriendRequest;
import com.healthpointsfitness.healthpointsfitness.models.User;
import com.healthpointsfitness.healthpointsfitness.repositories.FriendRequestsRepository;
import com.healthpointsfitness.healthpointsfitness.repositories.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import jakarta.activation.MimetypesFileTypeMap;

@RestController
@RequestMapping(path = "users/api/v1")
public class FriendsApiController {

    @Autowired
    FriendRequestsRepository repository;

    @Autowired
    private UserRepository userRepository;

    String defaultProfileImage = "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";

    @AllArgsConstructor
    @Getter
    @Setter
    private static class TransientFriendRequestResponse {
        String message;
        Boolean success;
    }

    @RequestMapping(
            name = "add_friend",
            path = "/add_friend",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    private TransientFriendRequestResponse addFriend(@RequestBody String toUsername) {
        try {
            User fromUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            fromUser = userRepository.findUserById(fromUser.getId());
            User toUser = userRepository.findUserByUsername(toUsername);
            System.out.println("Incoming Friend Request");
            System.out.println("From: " + fromUser.getUsername());
            System.out.println("To: " + toUser.getUsername());
            //Get all the previous request from the fromUser to the toUser.
            List<FriendRequest> previousRequests = repository.findFriendRequestByFromAndTo(fromUser, toUser);

            if (previousRequests.size() == 0) { //If there's no previous requests
                //Create a new friend request
                FriendRequest newFriendRequest = new FriendRequest();

                //Set it not approved initially
                newFriendRequest.setRequestApproved(false);

                //Set the fromUser to the logged in user
                newFriendRequest.setFrom(fromUser);

                //Set the toUser from the request
                newFriendRequest.setTo(toUser);

                //Set the date to now for date sent
                newFriendRequest.setDate_sent(Timestamp.from(Instant.now()));

                //Save the new friend request
                repository.save(newFriendRequest);

                //Let the user know the friend request was sent
                return new TransientFriendRequestResponse("Friend request has been sent.", true);

            } else {
                //Let the user know that there's already a friend request sent for that friend.
                return new TransientFriendRequestResponse("You have already sent that person a friend request.", false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new TransientFriendRequestResponse(e.getMessage(), false);
        }
    }

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    private static class FriendSearchRequest {
        Integer points;
        String searchTerm;

        Integer limit;

        Integer page;
    }

    @AllArgsConstructor
    @Setter
    @Getter
    private static class FriendSearchResponse {
        List<FriendRequest> allRequests;
        List<FriendRequest> friends;

        List<FriendRequest> sent_pending;

        List<FriendRequest> received_pending;

        List<FriendRequest> denied_requests;

        List<User> results;

        Integer numberOfResults;
    }

    private String getBaseStringForBlob(byte[] blob){
        try {

            InputStream is = new BufferedInputStream(new ByteArrayInputStream(blob));
            String mimeType = URLConnection.guessContentTypeFromStream(is);
            System.out.println(mimeType);
            System.out.println("Blob Length: " + blob.length);
            // Set the profile image data url for the to user
            byte[] encodeBase64 = Base64.getEncoder().encode(blob);
            String dataUrl = new String(encodeBase64, StandardCharsets.UTF_8);
            return "data:image/" + mimeType + ";base64," + dataUrl;
        }catch(Exception e){
            return "data:image/png;base64," + defaultProfileImage;
        }
    }

    @RequestMapping(
            name = "search_friends",
            path = "/search_friends",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    private FriendSearchResponse searchFriends(@RequestBody FriendSearchRequest request) {
        System.out.println("Hitting the /searchFriends post mapping");

        //Get the logged in user
        User me = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Grab all the friend requests
        List<FriendRequest> allRequest = repository.findAll().stream().peek(item -> {
            //Set the profile images
            item.getFrom().setProfileImageDataUrl(getBaseStringForBlob(item.getFrom().getProfileImage()));
            item.getTo().setProfileImageDataUrl(getBaseStringForBlob(item.getTo().getProfileImage()));


            //Clear sensitive data
            //Clear all sensitive data
            item.getTo().setPassword(null);
            item.getTo().setIsAdmin(null);
            item.getTo().setEmail(null);
            item.getTo().setProfileImage(null);
            item.getTo().setCompletedExercises(null);
            item.getTo().setCreated_paths(null);
            item.getTo().setFollowed_paths(null);
            item.getTo().setGoals(null);

            item.getFrom().setPassword(null);
            item.getFrom().setIsAdmin(null);
            item.getFrom().setEmail(null);
            item.getFrom().setProfileImage(null);
            item.getFrom().setCompletedExercises(null);
            item.getFrom().setCreated_paths(null);
            item.getFrom().setFollowed_paths(null);
            item.getFrom().setGoals(null);
        }).toList();

        // Friends
        List<FriendRequest> friends = allRequest
                .stream()
                .filter(friendRequest -> {
                    // Check if the request was approved
                    Boolean approved = friendRequest.getRequestApproved();

                    //Check for approve / deny date
                    Timestamp appDenDate = friendRequest.getDate_approved_or_denied();

                    // Get the user the request was sent to
                    User to = friendRequest.getTo();

                    // Get the user the request was sent by
                    User from = friendRequest.getFrom();

                    System.out.println("approved = " + approved);
                    System.out.println("appDenDate = " + appDenDate);
                    System.out.println("to.getUsername() = " + to.getUsername());
                    System.out.println("from.getUsername() = " + from.getUsername());

                    Boolean fullyApproved = approved && appDenDate != null;
                    System.out.println(fullyApproved);

                    // Any approved requests sent or received by me
                    return fullyApproved && ((Objects.equals(to.getId(), me.getId())) || (Objects.equals(from.getId(), me.getId())));
                }).toList();

        // Pending Sent Requests
        List<FriendRequest> pendingSentRequests = allRequest
                .stream()
                .filter(friendRequest -> {
                    //Set the images for the request

                    // Check if the request was approved
                    Boolean approved = friendRequest.getRequestApproved();

                    //Check for approve / deny date
                    Timestamp appDenDate = friendRequest.getDate_approved_or_denied();

                    // Get the user the request was sent by
                    User from = friendRequest.getFrom();

                    // Any denied requests sent by me
                    return !approved && appDenDate == null && (Objects.equals(from.getId(), me.getId()));
                }).toList();

        // List of denied sent / received requests
        List<FriendRequest> deniedRequests = allRequest
                .stream()
                .filter(friendRequest -> {
                    //Check if the request was denied
                    Boolean approved = friendRequest.getRequestApproved();

                    //Get timestamp
                    Timestamp timestamp = friendRequest.getDate_approved_or_denied();

                    if(timestamp != null && !approved){
                        return true;
                    }else{
                        return false;
                    }
                }).toList();

        // Pending Received Requests
        List<FriendRequest> pendingReceivedRequests = allRequest
                .stream()
                .filter(friendRequest -> {
                    //Set the profile images for the request

                    //Check if the request was approved
                    Boolean approved = friendRequest.getRequestApproved();

                    //Get the user the request was sent to
                    User to = friendRequest.getTo();

//                    if (to.getProfileImage() != null) {
//                        // Set the profile image data url for the to user
//                        byte[] encodeBase64 = Base64.getEncoder().encode(to.getProfileImage());
//                        String base64Encoded = new String(encodeBase64, StandardCharsets.UTF_8);
//                        to.setProfileImageDataUrl("data:image/jpeg;base64," + base64Encoded);
//                    } else {
//                        to.setProfileImageDataUrl(defaultProfileImage);
//                    }

//                    // Get the user the request was sent by
//                    User from = friendRequest.getFrom();

//                    if (from.getProfileImage() != null) {
//                        // Set the profile image data url for the to user
//                        byte[] encodeBase64_from = Base64.getEncoder().encode(from.getProfileImage());
//                        String base64Encoded_from = new String(encodeBase64_from, StandardCharsets.UTF_8);
//                        from.setProfileImageDataUrl("data:image/jpeg;base64," + base64Encoded_from);
//                    } else {
//                        from.setProfileImageDataUrl(defaultProfileImage);
//                    }

                    //Check for approve / deny date

                    Timestamp appDenDate = friendRequest.getDate_approved_or_denied();


                    //Any requests sent to me pending approval
                    return !approved && appDenDate == null && (Objects.equals(to.getId(), me.getId()));
                }).toList();

        //Get list of users under the points filter
        List<User> allResults = userRepository.findUserByTotalPointsLessThanEqual(request.points);

        //Filter that list by the search term
        allResults = allResults
                .stream()
                .filter(result -> result.getUsername()
                        .contains(request.searchTerm))
                .toList();

        //Clear all password fields
        allResults.forEach(result -> result.setPassword(null));

        //Clear crated paths fields
        allResults.forEach(result -> result.setCreated_paths(null));

        //Clear email field
        allResults.forEach(result -> result.setEmail(null));

        //Clear isAdmin field
        allResults.forEach(result -> result.setIsAdmin(null));

        //Map image pics to data urls
//        allResults.forEach(result -> {
//            byte[] image = result.getProfileImage();
//            if (image != null) {
//                if(result.getProfileImageDataUrl().length() == 0) {
//                    byte[] encodeBase64 = Base64.getEncoder().encode(image);
//                    String base64Encoded = new String(encodeBase64, StandardCharsets.UTF_8);
//                    result.setProfileImageDataUrl("data:image/jpeg;base64," + base64Encoded);
//                }
//            } else {
//                result.setProfileImageDataUrl("data:image/jpeg;base64," + defaultProfileImage);
//            }
//        });

        //Generate a result map to remove duplicates
        Map<Long, User> resultMap = new HashMap<>();

        //Stream the result list into the map
        allResults.forEach(result -> resultMap.put(result.getId(), result));

        //Convert the map values back to a list
        List<User> finalResultsWithoutDuplicates = resultMap.values().stream().toList();

        //Remove me from the list
        finalResultsWithoutDuplicates = finalResultsWithoutDuplicates
                .stream()
                .filter(result -> !result.getUsername().equals(me.getUsername()))
                .toList()
                .stream()
                .peek(item -> {
//                    item.setProfileImageDataUrl(getBaseStringForBlob(item.getProfileImage()));
                    //Clear all sensitive data
                    item.setPassword(null);
                    item.setIsAdmin(null);
                    item.setEmail(null);
                    item.setProfileImage(null);
                    item.setCompletedExercises(null);
                    item.setCreated_paths(null);
                    item.setFollowed_paths(null);
                    item.setGoals(null);


                }).toList();

        System.out.println("============================= REQUESTING FRIENDS ============================");
        System.out.println("Page: " + request.page);
        System.out.println("Limit: " + request.limit);
        System.out.println("Available Results: " + finalResultsWithoutDuplicates.size());

        //Storage for the paginated list
        List<User> paginatedList = new ArrayList<>();

        //Get the start and end of the page for pagination logic
        int pageStart = request.page * request.limit;
        int pageEnd = pageStart + request.limit;

        //Pagination logic
        if(finalResultsWithoutDuplicates.size() >= pageEnd){
            paginatedList = finalResultsWithoutDuplicates.subList(pageStart,pageEnd);
        }if(finalResultsWithoutDuplicates.size() <= pageEnd && finalResultsWithoutDuplicates.size() > pageStart){
            paginatedList = finalResultsWithoutDuplicates.subList(pageStart,finalResultsWithoutDuplicates.size());
        }

        //Return a new friend search response to the client
        return new FriendSearchResponse(allRequest, friends, pendingSentRequests, pendingReceivedRequests, deniedRequests ,paginatedList,finalResultsWithoutDuplicates.size());

    }


    @Getter
    @Setter
    @AllArgsConstructor
    private static class FriendRequestDeleteResponse {
        Boolean success;
        String message;
    }

    @RequestMapping(
            name = "delete_friend_request",
            path = "/delete_friend_request",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    private FriendRequestDeleteResponse deleteFriendRequest(@RequestBody String username) {
        try {
            //Get the user from Spring Security
            User me = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            //Get the user from the user repository using the id from the spring sec one
            Optional<User> meOption = userRepository.findById(me.getId());
            if (meOption.isPresent()) {
                me = meOption.get();
            }

            //Get the friend user
            User friend = userRepository.findUserByUsername(username);

            //Find the friend request from me to the friend
            List<FriendRequest> friendRequest = repository.findFriendRequestByFromAndTo(me, friend);

            //Iterate through the list of friend requests and delete them
            repository.deleteAll(friendRequest);

            //Return a response to the user letting them know the request was deleted.
            return new FriendRequestDeleteResponse(true, "Friend request deleted.");
        } catch (Exception e) { //If an error is encountered

            //Print the stack trace
            e.printStackTrace();

            //Return a message to the client with the error
            return new FriendRequestDeleteResponse(false, e.getMessage());
        }
    }


    @AllArgsConstructor
    @Getter
    @Setter
    private static class FriendRequestDenyResponse {
        Boolean success;
        String message;
    }

    @RequestMapping(
            name = "deny_friend_request",
            path = "/deny_friend_request",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    private FriendRequestDenyResponse denyFriendRequest(@RequestBody String username) {
        try {
            //Get user from spring security
            User me = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            //Grab the user from the user repo using the spring sec id
            Optional<User> meOption = userRepository.findById(me.getId());
            if (meOption.isPresent()) {
                me = meOption.get();
            }

            //Grab the friend
            User friend = userRepository.findUserByUsername(username);

            //Grab the friend request
            List<FriendRequest> requests = repository.findFriendRequestByFromAndTo(friend, me);

            //Iterate through each request
            requests.forEach(req -> {
                //Set the timestamp for when it was denied
                req.setDate_approved_or_denied(Timestamp.from(Instant.now()));

                //Set to denied
                req.setRequestApproved(false);

                //Save it
                repository.save(req);
            });

            //Let the user know that the friend request was denizled
            return new FriendRequestDenyResponse(true, "Friend request denied.");

        } catch (Exception e) { //Catch any errors
            //Print them to the terminal
            e.printStackTrace();

            //Send the error back to the client
            return new FriendRequestDenyResponse(false, e.getMessage());
        }
    }


    @AllArgsConstructor
    @Getter
    @Setter
    private static class FriendRequestAcceptResponse {
        Boolean success;
        String message;
    }

    @RequestMapping(
            name = "accept_friend_request",
            path = "/accept_friend_request",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    private FriendRequestAcceptResponse acceptFriendRequest(@RequestBody String username) {
        try {
            //Get user from spring security
            User me = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            //Grab the user from the user repo using the spring sec id
            Optional<User> meOption = userRepository.findById(me.getId());
            if (meOption.isPresent()) {
                me = meOption.get();
            }

            //Grab the friend
            User friend = userRepository.findUserByUsername(username);

            //Grab the friend request
            List<FriendRequest> requests = repository.findFriendRequestByFromAndTo(friend, me);

            //Iterate through each request
            requests.forEach(req -> {
                //Set the timestamp for when it was accepted
                req.setDate_approved_or_denied(Timestamp.from(Instant.now()));

                //Set to accepted
                req.setRequestApproved(true);

                //Save it
                repository.save(req);
            });

            //Let the user know that the friend request was accepted
            return new FriendRequestAcceptResponse(true, "Friend request accepted.");

        } catch (Exception e) { //Catch any errors
            //Print them to the terminal
            e.printStackTrace();

            //Send the error back to the client
            return new FriendRequestAcceptResponse(false, e.getMessage());
        }
    }


    @AllArgsConstructor
    @Getter
    @Setter
    private static class UnfriendRequestResponse {
        Boolean success;
        String message;
    }

    @RequestMapping(
            name = "unfriend_request",
            path = "/unfriend_request",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    private UnfriendRequestResponse unfriendRequest(@RequestBody String username) {
        try {

            System.out.println("Hitting unfriend route.");
            //Get user from spring security
            User me = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            //Grab the user from the user repo using the spring sec id
            Optional<User> meOption = userRepository.findById(me.getId());
            if (meOption.isPresent()) {
                me = meOption.get();
            }

            //Grab the friend
            User friend = userRepository.findUserByUsername(username);

            //Grab the friend request
            List<FriendRequest> incomingRequests = repository.findFriendRequestByFromAndTo(friend, me);
            List<FriendRequest> outgoingRequests = repository.findFriendRequestByFromAndTo(me, friend);

            List<FriendRequest> allFriendRequests = ListUtils.sum(incomingRequests,outgoingRequests);

            //Iterate through each request
            allFriendRequests.forEach(req -> {
                //Set the timestamp for when it was accepted
                req.setDate_approved_or_denied(null);

                //Set to not accepted
                req.setRequestApproved(false);

                //Save it
                repository.save(req);
            });

            //Let the user know that the friend was unfriended
            return new UnfriendRequestResponse(true, "Friend unfriended.");

        } catch (Exception e) { //Catch any errors
            //Print them to the terminal
            e.printStackTrace();

            //Send the error back to the client
            return new UnfriendRequestResponse(false, e.getMessage());
        }
    }

    @AllArgsConstructor
    @Getter
    @Setter
    private static class UnblockRequestResponse {
        Boolean success;
        String message;
    }

    @RequestMapping(
            name = "unblock_request",
            path = "/unblock_request",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    private UnblockRequestResponse unblockRequest(@RequestBody String username) {
        try {
            //Get user from spring security
            User me = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            //Grab the user from the user repo using the spring sec id
            Optional<User> meOption = userRepository.findById(me.getId());
            if (meOption.isPresent()) {
                me = meOption.get();
            }

            //Grab the friend
            User friend = userRepository.findUserByUsername(username);

            //Grab the friend request
            List<FriendRequest> requests = repository.findFriendRequestByFromAndTo(friend, me);

            //Iterate through each request
            requests.forEach(req -> {
                //Set the timestamp for when it was accepted
                req.setDate_approved_or_denied(null);

                //Set to not accepted
                req.setRequestApproved(false);

                //Save it
                repository.save(req);
            });

            //Let the user know that the friend was unblocked
            return new UnblockRequestResponse(true, "Friend unblocked.");

        } catch (Exception e) { //Catch any errors
            //Print them to the terminal
            e.printStackTrace();

            //Send the error back to the client
            return new UnblockRequestResponse(false, e.getMessage());
        }
    }
}
