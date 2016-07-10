package ge.reklapp.service;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Tornike on 10.07.2016.
 */
@XmlRootElement
public class StatusResponse {

    public StatusResponse(String problem){
        this.problem = problem;
    }

    public StatusResponse(){}

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    @XmlElement(name = "problem")
    private String problem;

    @Override
    public int hashCode() {
        if (problem != null)
            return problem.hashCode();
        else
            return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        StatusResponse other = (StatusResponse) obj;
        return this.problem.equals(other.getProblem());
    }
}