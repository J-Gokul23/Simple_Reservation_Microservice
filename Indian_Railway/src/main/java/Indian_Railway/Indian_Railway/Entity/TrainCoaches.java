package Indian_Railway.Indian_Railway.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainCoaches {
    @Id
    @SequenceGenerator(name = "seqcoaches",sequenceName = "seqtrainCoaches",allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "seqcoaches")
    private Integer id;
    private Integer total_no_of_coaches;
    private Integer no_of_sleepercoaches;
    private Integer no_of_ACcoaches;
    private Integer no_of_Generalcoaches;
    private Integer no_of_reserved_coache;
    private Integer no_of_Luggagecoaches;
}
