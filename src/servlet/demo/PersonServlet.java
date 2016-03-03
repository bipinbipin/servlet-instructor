package servlet.demo;

import com.astontech.bo.Person;
import com.astontech.dao.PersonDAO;
import com.astontech.dao.mysql.PersonDAOImpl;
import common.helpers.DateHelper;
import common.helpers.ServletHelper;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Enumeration;

/**
 * Created by Bipin on 2/29/2016.
 */
public class PersonServlet extends HttpServlet {

    final static Logger logger = Logger.getLogger(PersonServlet.class);
    private static PersonDAO personDAO = new PersonDAOImpl();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


        switch (request.getParameter("formName")) {

            case "choosePerson":
                form1_choosePerson(request);
                break;

            case "updatePerson":
                form2_updatePerson(request);
                break;

            default:
                break;

        }


        request.getRequestDispatcher("./person.jsp").forward(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        request.setAttribute("personId", "0");
        request.setAttribute("selectPerson", generatePersonDropDownHTML(0));

        request.setAttribute("personList", personDAO.getPersonList());

        request.getRequestDispatcher("./person.jsp").forward(request, response);
    }

    private static String generatePersonDropDownHTML(int selectedPersonId) {

        /*
            <select name="selectPerson">
                <option value="1">Bipin</option>
                <option value="2">Dan</option>
                <option value="3">Sean</option>
                <option value="4">Adrian</option>
                <option value="5">James</option>
            </select>
         */

        StringBuilder strBld = new StringBuilder();
        strBld.append("<select name='selectPerson'>");

        for(Person person : personDAO.getPersonList()) {
            if(person.getPersonId() == selectedPersonId)
                strBld.append("<option selected value='").append(person.getPersonId()).append("'>")
                        .append(person.GetFullName()).append("</option>");
            else
                strBld.append("<option value='").append(person.getPersonId()).append("'>")
                    .append(person.GetFullName()).append("</option>");
        }

        strBld.append("</select>");

        return strBld.toString();
    }

    private static void form1_choosePerson(HttpServletRequest request) {

        logger.info("Form #1 submit - Form Name = " + request.getParameter("formName"));
        ServletHelper.logRequestParams(request);

        //notes:    everything comes back from request as a STRING!
        String selectedPersonId = request.getParameter("selectPerson");


        Person selectedPerson = personDAO.getPersonById(Integer.parseInt(selectedPersonId));

        logger.info("Selected Person Detail: " + selectedPerson.ToString());

        //todo:     now place those details in form #2
        personToRequest(request, selectedPerson);

        //notes:    generate dropdown with HTML
        request.setAttribute("selectPerson", generatePersonDropDownHTML(Integer.parseInt(request.getParameter("selectPerson"))));

        //notes:    generate dropdown using JSTL
        request.setAttribute("personList", personDAO.getPersonList());
    }

    private static void form2_updatePerson(HttpServletRequest request) {

        logger.info("Form #2 submit - Form Name = " + request.getParameter("formName"));
        ServletHelper.logRequestParams(request);

        Person updatePerson = new Person();
        requestToPerson(request, updatePerson);

        logger.info(updatePerson.ToString());
        personDAO.updatePerson(updatePerson);

        //notes:    inefficient! extra call to the database.
        updatePerson = personDAO.getPersonById(updatePerson.getPersonId());
        personToRequest(request, updatePerson);

        //notes:    generate dropdown with HTML
        request.setAttribute("selectPerson", generatePersonDropDownHTML(Integer.parseInt(request.getParameter("personId"))));

        //notes:    generate dropdown using JSTL
        request.setAttribute("personList", personDAO.getPersonList());
    }

    private static void requestToPerson(HttpServletRequest request, Person person) {

        person.setPersonId(Integer.parseInt(request.getParameter("personId")));
        person.setFirstName(request.getParameter("firstName"));
        person.setMiddleName(request.getParameter("middleName"));
        person.setLastName(request.getParameter("lastName"));
        person.setBirthDate(DateHelper.stringToUtilDate(request.getParameter("birthDate"), "yyyy-MM-dd"));
        person.setSocialSecurityNumber(request.getParameter("socialSecurityNumber"));

    }

    private static void personToRequest(HttpServletRequest request, Person person) {

        request.setAttribute("personId", person.getPersonId());
        request.setAttribute("firstName", person.getFirstName());
        request.setAttribute("middleName", person.getMiddleName());
        request.setAttribute("lastName", person.getLastName());
        request.setAttribute("birthDate", person.getBirthDate());
        request.setAttribute("socialSecurityNumber", person.getSocialSecurityNumber());

    }
}
