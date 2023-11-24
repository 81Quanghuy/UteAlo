package vn.iostar.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

// them entity file de luu file
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "files")
public class FilesMedia implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;
        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        private String fileId;
        private String name;
        private String type;
        private Long size;
        private String url;

        @ManyToOne
        @JoinColumn(name = "messageId")
        private Message message;

        private Date createAt;
        private Date updateAt;

}
