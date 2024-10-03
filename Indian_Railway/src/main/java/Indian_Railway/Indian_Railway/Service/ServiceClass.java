package Indian_Railway.Indian_Railway.Service;

import Indian_Railway.Indian_Railway.Entity.TrainDetails;
import Indian_Railway.Indian_Railway.Entity.TrainReservationSystem;
import Indian_Railway.Indian_Railway.Repository.*;
import Indian_Railway.Indian_Railway.Wrapper.PassengerTicketBooking;
import Indian_Railway.Indian_Railway.Wrapper.TicketCheckingWrapper;
import Indian_Railway.Indian_Railway.Wrapper.TrainStoppingStationWrapper;
import Indian_Railway.Indian_Railway.Wrapper.TrainWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ServiceClass {

    private TrainDetailsRepo trainDetailsRepo;

    private StationDetailsRepo stationDetailsRepo;

    private TrainStoppingStationRepo trainStoppingStationRepo;

    private TrainCoachesRepo trainCoachesRepo;

    private TrainReservationSystemRepo trainReservationSystemRepo;

    @Autowired
    public ServiceClass(TrainDetailsRepo trainDetailsRepo, StationDetailsRepo stationDetailsRepo, TrainStoppingStationRepo trainStoppingStationRepo, TrainCoachesRepo trainCoachesRepo, TrainReservationSystemRepo trainReservationSystemRepo) {
        this.trainDetailsRepo = trainDetailsRepo;
        this.stationDetailsRepo = stationDetailsRepo;
        this.trainStoppingStationRepo = trainStoppingStationRepo;
        this.trainCoachesRepo = trainCoachesRepo;
        this.trainReservationSystemRepo = trainReservationSystemRepo;
    }

    public ResponseEntity<List<TrainDetails>> GetAll() {
        List getall = trainReservationSystemRepo.findAll();
        return new ResponseEntity<>(getall, HttpStatus.ACCEPTED);
    }

    public ResponseEntity<String> AddOneTrain(TrainDetails trainDetails) {
        trainDetails.setNo_of_stoppingstations(trainDetails.getTrainStoppingStations().size());
        trainDetails.getTrainCoaches().setTotal_no_of_coaches((trainDetails.getTrainCoaches()
                .getNo_of_Generalcoaches())
                        +(trainDetails.getTrainCoaches().getNo_of_sleepercoaches())
                        +(trainDetails.getTrainCoaches().getNo_of_Luggagecoaches())
                        +(trainDetails.getTrainCoaches().getNo_of_ACcoaches())
                        +(trainDetails.getTrainCoaches().getNo_of_reserved_coache()));
        LocalDateTime departure_time = trainDetails.getTrainStoppingStations().get(0).getDeparture_date_time();
        trainDetails.getTrainReservationSystem()
                        .setReservation_closes_at(departure_time.minusHours(12));
        trainDetails.getTrainReservationSystem().setReservation_opens_at(LocalDateTime.now());
        trainDetails.getTrainReservationSystem().setTotal_no_reservation_tickets(
                (trainDetails.getTrainReservationSystem().getNo_ac_tickets()) +
                        (trainDetails.getTrainReservationSystem().getNo_general_reserve_tickets())+
                        (trainDetails.getTrainReservationSystem().getNo_sleeper_tickets()));
        trainDetails.getTrainReservationSystem().setTrain_number(trainDetails.getTrain_number());
        trainDetailsRepo.save(trainDetails);
        return new ResponseEntity<>("One Train Added",HttpStatus.CREATED);
    }

    public void AddAllRail(List<TrainDetails> trainDetails) {
        trainDetailsRepo.saveAll(trainDetails);
    }

    public ResponseEntity<TrainDetails> GetTrainbyName(String trainname) {
        return new ResponseEntity<>(trainDetailsRepo.findbytrain_name(trainname),HttpStatus.FOUND);
    }

    public ResponseEntity<TrainWrapper> Get_TrainWrapper_ByTrainNumber(int trainNumber) {
        TrainDetails trainDetails = trainDetailsRepo.findByTrain_Number(trainNumber);
        TrainWrapper trainWrapper = new TrainWrapper();
        trainWrapper.setTrain_number(trainDetails.getTrain_number());
        trainWrapper.setTrain_name(trainDetails.getTrain_name());
        trainWrapper.setStarting_point(trainDetails.getStarting_point());
        trainWrapper.setDestination(trainDetails.getDestination());
        List<TrainStoppingStationWrapper> stoppingStationWrappers = new ArrayList<>();
        for (int i = 0; i < trainDetails.getTrainStoppingStations().size(); i++) {
            String station_name = trainDetails.getTrainStoppingStations().get(i).getStation_name();
            Integer platform_number = trainDetails.getTrainStoppingStations().get(i).getPlatform_no();
            TrainStoppingStationWrapper trainStoppingStationWrapper = new TrainStoppingStationWrapper();
            trainStoppingStationWrapper.setStation_name(station_name);
            trainStoppingStationWrapper.setPlatform_no(platform_number);
            stoppingStationWrappers.add(trainStoppingStationWrapper);
        }

        trainWrapper.setTrainStoppingStationWrapperList(stoppingStationWrappers);
        trainWrapper.setNo_stopping_stations(trainDetails.getNo_of_stoppingstations());
        return new ResponseEntity<>(trainWrapper,HttpStatus.OK);
    }

    public ResponseEntity<TrainDetails> GetDetailsTrainByNumber(int trainNumber) {
        return new ResponseEntity<>(trainDetailsRepo.findByTrain_Number(trainNumber),HttpStatus.FOUND);
    }

    public ResponseEntity<TicketCheckingWrapper> check_all_coach_ticket_availability(int trainnumber){
         TrainDetails trainDetails = trainDetailsRepo.findByTrain_Number(trainnumber);
         TicketCheckingWrapper ticketCheckingWrapper = new TicketCheckingWrapper();
         ticketCheckingWrapper.setTrain_name(trainDetails.getTrain_name());
         ticketCheckingWrapper.setTrain_number(trainDetails.getTrain_number());
         ticketCheckingWrapper.setNo_general_reserve_tickets(trainDetails.getTrainReservationSystem().getNo_general_reserve_tickets());
         ticketCheckingWrapper.setNo_ac_tickets(trainDetails.getTrainReservationSystem().getNo_ac_tickets());
         ticketCheckingWrapper.setNo_sleeper_tickets(trainDetails.getTrainReservationSystem().getNo_sleeper_tickets());
         ticketCheckingWrapper.setReservation_closes_at(trainDetails.getTrainReservationSystem().getReservation_closes_at());
         ticketCheckingWrapper.setReservation_opens_at(trainDetails.getTrainReservationSystem().getReservation_opens_at());
         return new ResponseEntity<>(ticketCheckingWrapper,HttpStatus.OK);
    }
    public ResponseEntity<PassengerTicketBooking> ticket_booking(int trainNumber, String coach, int noOfTickets) {
        TrainReservationSystem trainReservationSystem = trainReservationSystemRepo.findByTrain_Number(trainNumber);
        PassengerTicketBooking passengerTicketBooking = new PassengerTicketBooking();
        Integer ticket_Available;
        String Message = "";
        if(coach.trim().equalsIgnoreCase("AC")){
             ticket_Available = trainReservationSystem.getNo_ac_tickets();
             Message = Ac_ticket_booking(trainNumber,ticket_Available,noOfTickets,passengerTicketBooking);
             if(Message.equalsIgnoreCase("success")){
                 passengerTicketBooking1(passengerTicketBooking,coach.toUpperCase()+" Ticket :"+noOfTickets,trainNumber);
             }

        } else if (coach.trim().equalsIgnoreCase("Sleeper")) {
            ticket_Available = trainReservationSystem.getNo_sleeper_tickets();

            Message = Ac_ticket_booking(trainNumber,ticket_Available,noOfTickets,passengerTicketBooking);
            if(Message.equalsIgnoreCase("success")){
                passengerTicketBooking1(passengerTicketBooking,coach.toUpperCase()+" Ticket :"+noOfTickets,trainNumber);

            }

        }else {
            ticket_Available = trainReservationSystem.getNo_general_reserve_tickets();

            Message = Ac_ticket_booking(trainNumber,ticket_Available,noOfTickets,passengerTicketBooking);
            if(Message.equalsIgnoreCase("success")){
                passengerTicketBooking1(passengerTicketBooking,coach.toUpperCase()+" Ticket :"+noOfTickets,trainNumber);
                //   passengerTicketBooking.setNo_of_tickets("Ac Ticket : "+noOfTickets);
            }

        }
        if(Message.equalsIgnoreCase("Success")){
            return new ResponseEntity<>(passengerTicketBooking,HttpStatus.OK);
        }else {
            return new ResponseEntity<>(passengerTicketBooking,HttpStatus.BAD_REQUEST);
        }
    }

    private String Ac_ticket_booking(int trainNumber, Integer ticketAvailable, int noOfTickets,PassengerTicketBooking passengerTicketBooking) {
        if (noOfTickets<=ticketAvailable){
            noOfTickets = (ticketAvailable-noOfTickets);
            trainReservationSystemRepo.updateAcTicket(trainNumber,noOfTickets);
            passengerTicketBooking.setTicket_conformation("Success");
            passengerTicketBooking.setReserved_on_datetime(LocalDateTime.now());
            return "Success";
        }else{
            passengerTicketBooking.setTicket_conformation("Reservation Failed");
            passengerTicketBooking.setReserved_on_datetime(LocalDateTime.now());
            return "Reservation Failed";
        }
    }
    private String Sleeper_ticket_booking(int trainNumber, Integer ticketAvailable, int noOfTickets,PassengerTicketBooking passengerTicketBooking) {
        if (noOfTickets<=ticketAvailable){
            noOfTickets = (ticketAvailable-noOfTickets);
            trainReservationSystemRepo.updateSleeperTicket(trainNumber,noOfTickets);
            passengerTicketBooking.setTicket_conformation("Success");
            passengerTicketBooking.setReserved_on_datetime(LocalDateTime.now());
            return "Success";
        }else{
            passengerTicketBooking.setTicket_conformation("Reservation Failed");
            passengerTicketBooking.setReserved_on_datetime(LocalDateTime.now());
            return "Reservation Failed";
        }
    }
    private String General_reserve_ticket_booking(int trainNumber, Integer ticketAvailable, int noOfTickets,PassengerTicketBooking passengerTicketBooking) {
        if (noOfTickets<=ticketAvailable){
            noOfTickets = (ticketAvailable-noOfTickets);
            trainReservationSystemRepo.updateGeneralReserveTicket(trainNumber,noOfTickets);
            passengerTicketBooking.setTicket_conformation("Success");
            passengerTicketBooking.setReserved_on_datetime(LocalDateTime.now());
            return "Success";
        }else{
            passengerTicketBooking.setTicket_conformation("Reservation Failed");
            passengerTicketBooking.setReserved_on_datetime(LocalDateTime.now());
            return "Reservation Failed";
        }
    }

    private void passengerTicketBooking1(PassengerTicketBooking passengerTicketBooking,String noOfTickets,int trainNumber) {
        TrainDetails trainDetails = trainDetailsRepo.findByTrain_Number(trainNumber);
       passengerTicketBooking.setTrain_name(trainDetails.getTrain_name());
       passengerTicketBooking.setTrain_number(trainDetails.getTrain_number());
       passengerTicketBooking.setStarting_point(trainDetails.getStarting_point());
       passengerTicketBooking.setDestination(trainDetails.getDestination());
       passengerTicketBooking.setNo_of_tickets(noOfTickets);
       passengerTicketBooking.setReserved_on_datetime(LocalDateTime.now());
    }

    public String delete_train_trainid(int trainNumber) {
        trainDetailsRepo.deleteById(trainNumber);
        return "Train Deleted";
    }

}
