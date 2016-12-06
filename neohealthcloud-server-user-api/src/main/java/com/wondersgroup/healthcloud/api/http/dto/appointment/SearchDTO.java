package com.wondersgroup.healthcloud.api.http.dto.appointment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by longshasha on 16/12/5.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchDTO {

    @JsonProperty("doctor")
    private SearchDoctorDTO doctor;

    @JsonProperty("hospital")
    private SearchHospitalDTO hospital;

    public SearchDTO(){

    }


    public SearchDoctorDTO getDoctor() {
        return doctor;
    }

    public void setDoctor(SearchDoctorDTO doctor) {
        this.doctor = doctor;
    }

    public SearchHospitalDTO getHospital() {
        return hospital;
    }

    public void setHospital(SearchHospitalDTO hospital) {
        this.hospital = hospital;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public class SearchDoctorDTO{

        private Boolean more;
        private List<AppointmentDoctorDTO> content;


        public SearchDoctorDTO(List<AppointmentDoctorDTO> content, Boolean more) {
            this.more = more;
            this.content = content;
        }

        public Boolean getMore() {
            return more;
        }

        public void setMore(Boolean more) {
            this.more = more;
        }

        public List<AppointmentDoctorDTO> getContent() {
            return content;
        }

        public void setContent(List<AppointmentDoctorDTO> content) {
            this.content = content;
        }
    }


    @JsonInclude(JsonInclude.Include.NON_NULL)
    public class SearchHospitalDTO{
        private Boolean more;
        private List<AppointmentHospitalDTO> content;


        public SearchHospitalDTO(List<AppointmentHospitalDTO> content, Boolean more) {
            this.more = more;
            this.content = content;
        }

        public Boolean getMore() {
            return more;
        }

        public void setMore(Boolean more) {
            this.more = more;
        }

        public List<AppointmentHospitalDTO> getContent() {
            return content;
        }

        public void setContent(List<AppointmentHospitalDTO> content) {
            this.content = content;
        }
    }
}



