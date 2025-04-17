package org.example.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.entities.Ticket;
import org.example.entities.Train;
import org.example.entities.User;
import org.example.util.UserServiceUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class UserBookingService
{
    private User user;
    private List<User> userList;
    private ObjectMapper objectMapper = new ObjectMapper();
    private static final String USERS_PATH = "C:\\Users\\HP\\PROJECT\\app\\src\\main\\resources\\org\\example\\localDb\\users.json";

    public UserBookingService(User user1) throws IOException
    {
        this.user = user1;
       loadUser();


    }
    public UserBookingService()throws IOException{
        loadUser();

    }
    public List<User> loadUser()throws IOException{
        System.out.println("Trying to read users.json from: " + new File(USERS_PATH).getAbsolutePath());
        File users = new File(USERS_PATH);

        if (users.length() == 0) {
            // If file is empty, initialize an empty list
            userList = new ArrayList<>();
        } else {
            // Otherwise, read the JSON into the userList
            userList = objectMapper.readValue(users, new TypeReference<List<User>>() {});
        }

        return userList;

    }
    public Boolean loginUser(){
        Optional<User> foundUser = userList.stream().filter(user1 -> {
            return user1.getName().equals(user.getName()) && UserServiceUtil.checkPassword(user.getPassword(), user1.getHashedPassword());
        }).findFirst();
        return foundUser.isPresent();
    }

    public Boolean signUp(User user1){
        try{
            userList.add(user1);
            saveUserListToFile();
            return Boolean.TRUE;
        }catch (IOException ex){
            return Boolean.FALSE;
        }
    }
    private void saveUserListToFile() throws IOException {
        File usersFile = new File(USERS_PATH);
        objectMapper.writeValue(usersFile, userList);


    }
    public void fetchBooking(){
        Optional<User> userFetched = userList.stream().filter(user1 -> {
            return user1.getName().equals(user.getName()) && UserServiceUtil.checkPassword(user.getPassword(), user1.getHashedPassword());
        }).findFirst();
        if(userFetched.isPresent()){
            userFetched.get().printTickets();
        }

    }
    public boolean cancelBooking(String ticketId){
        Scanner s = new Scanner(System.in);
        System.out.println("Enter the Ticket id to cancel");
        ticketId = s.next();

        if(ticketId == null || ticketId.isEmpty()){
            System.out.println("Ticket id cannot be null empty.");
            return Boolean.FALSE;
        }
        String finalTicketId1 = ticketId;
        boolean remove = user.getTicketsBooked().removeIf(ticket -> ticket.getTicketId().equals(finalTicketId1) );

        String finalTicketId = ticketId;
        user.getTicketsBooked().removeIf(Ticket -> Ticket.getTicketId().equals(finalTicketId));
        if(remove){
            System.out.println("Ticket with ID"+ ticketId + "has been canceled.");
            return Boolean.TRUE;
        }else {
            System.out.println("No ticket found with ID "+ ticketId);
            return Boolean.FALSE;
        }
    }
    public List<Train> getTrains(String source, String destination){
        try{
            TrainService trainService = new TrainService();
            return trainService.searchTrains(source, destination);
        }catch(IOException ex){
            return new ArrayList<>();
        }
    }
    public List<List<Integer>> fetchSeats(Train train){
        return train.getSeats();
    }
    public Boolean bookTrainSeats(Train train, int row, int seat){
        try{
            TrainService trainService = new TrainService();
            List<List<Integer>> seats = train.getSeats();
            if(row >= 0 && row <seats.size() && seat >= 0 && seat < seats.get(row).size()){
                if(seats.get(row).get(seat) == 0){
                    seats.get(row).set(seat, 1);
                    train.setSeats(seats);
                    trainService.addTrain(train);
                    return true; //Booking successful
                } else {
                    return false; // Seat is already booked
                }
            } else {
                return false; // Invalid row or seat index
            }
        }catch (IOException ex){
            return Boolean.FALSE;
        }
    }


}
