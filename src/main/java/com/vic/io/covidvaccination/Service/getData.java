package com.vic.io.covidvaccination.Service;

import com.vic.io.covidvaccination.Model.Centers;
import com.vic.io.covidvaccination.Model.Response;
import com.vic.io.covidvaccination.Model.SessionList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class getData {
    @Autowired
    private RestTemplate restTemplate;
    private final LocalDate date=LocalDate.now();
    DateTimeFormatter dataFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");


    private String getByDistrict ="https://cdn-api.co-vin.in/api/v2/appointment/sessions/public/calendarByDistrict";

    // FIXME: 17/06/21 Null Pointer Exception

    public List<Centers> getDetails(String district_id){
       UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getByDistrict)
              .queryParam("district_id",district_id)
              .queryParam("date",date.format(dataFormatter));
        return restTemplate.getForObject(builder.toUriString(), Response.class).getCenters();
   }


//check for available centers

   public List<Centers> getAvailability(String district_id){
        List<Centers> centers=getDetails(district_id);
       List<Centers> centersList1=new ArrayList<>();

       for (Centers centers1:centers){
            List<SessionList> sessionLists=centers1.getSessions();
            List<SessionList> collect = sessionLists.stream()
                    .filter(sessionList -> sessionList.getAvailable_capacity() >= 1)
                    .collect(Collectors.toList());

            if (!collect.isEmpty()){
                centers1.setSessions(collect);
                centersList1.add(centers1);
            }
        }
//       System.out.println("################");
//       centersList1.forEach(System.out::println);
//       System.out.println("################");


       return centersList1;
   }



}

