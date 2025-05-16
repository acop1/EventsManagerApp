package com.example.eventsmanager.model;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PrivateEventItem extends EventItem {

    HashSet<String> invitations = new HashSet<>();
    public static final String INVITATIONS = "invitations";
    //private event constructor
    public PrivateEventItem(String location, String author, String title, String time, String description, String date) {
        super(location, author, title, time, description, date);
        System.out.println("Creating a private event item");
    }

    public PrivateEventItem(){}

    public HashSet<String> getInvitations() {
        return this.invitations;
    }

    //a method that adds Invitations. executed in runAddInvites from CmdLineUI class
    public void addInvitations(String[] invitees){
        this.invitations.addAll(Arrays.asList(invitees));
    }

    //a method that removes Invitations. executed in runAddInvites from CmdLineUI class
    public void removeInvitations(String[] invitees) {
        if (invitees == null || this.invitations == null) {
            return;
        }
        this.invitations.removeAll(Arrays.asList(invitees));
    }

    @Override
    //toString for private events
    public String toString(){
        return "\nPrivate Event:\nTitle: " + this.title + ", Author: " + this.author+ ", Date: "+getDateString() + ", Time: " + getTimeString() +
                ", Location: " + this.location +"\nDescription: " + this.description + "\nEvent Invitees: " + this.invitations+"\n";
    }

    @NonNull
    public Map<String, Object> toMap() {
        Map<String, Object> map = super.toMap();
        map.put("invitations", new ArrayList<>(this.invitations));
        return map;
    }

    @NonNull
    public static PrivateEventItem fromMap(@NonNull Map<String, Object> map) {
        final PrivateEventItem eventItem = new PrivateEventItem();

        eventItem.location = map.containsKey(LOCATION) ? String.valueOf(map.get(LOCATION)) : "";
        eventItem.author = map.containsKey(AUTHOR) ? String.valueOf(map.get(AUTHOR)) : "";
        eventItem.title = map.containsKey(TITLE) ? String.valueOf(map.get(TITLE)) : "";
        eventItem.description = map.containsKey(DESCRIPTION) ? String.valueOf(map.get(DESCRIPTION)) : "";
        //eventItem.invitations = map.containsKey(INVITATIONS) ? map.get(INVITATIONS) : null;
        if(map.containsKey(INVITATIONS)) {
            for (Object name : (List) map.get(INVITATIONS)) {
                eventItem.invitations.add((String) name);
            }
        }
        else{
            eventItem.invitations = null;
        }


        // Handle time and date with null checks
        String timeStr = map.containsKey(TIME) ? String.valueOf(map.get(TIME)) : "00:00";
        String dateStr = map.containsKey(DATE) ? String.valueOf(map.get(DATE)) : "01/01/2000";

        eventItem.time = parseTime(timeStr);
        eventItem.date = parseDate(dateStr);

        return eventItem;
    }
}




