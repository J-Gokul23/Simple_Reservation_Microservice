package Indian_Railway.Indian_Railway.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrainDetails {

    @Id
    @SequenceGenerator(name = "traindetailsseq",sequenceName = "seqtraindetails", allocationSize = 1,initialValue = 100)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "traindetailsseq")
    private Integer id;
    @Column(unique = true)
    private Integer train_number;
    private String train_name;
    private String starting_point;
    private String destination;
    private Integer no_of_stoppingstations;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "train_trainCoaches_id")
    private TrainCoaches trainCoaches;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "train_number",referencedColumnName = "train_number")
    private List<TrainStoppingStation> trainStoppingStations;

    @OneToOne(cascade = CascadeType.ALL)
    private TrainReservationSystem trainReservationSystem;
}
