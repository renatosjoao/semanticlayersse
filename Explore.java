//package org.home;
//
//import java.io.IOException;
//import java.util.ArrayList;
//
//import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
///**
// * Servlet implementation class Explore
// */
//@WebServlet("/Explore")
//public class Explore extends HttpServlet {
//	private static final long serialVersionUID = 1L;
//       
//    /**
//     * @see HttpServlet#HttpServlet()
//     */
//    public Explore() {
//        super();
//    }
//
//	/**
//	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
//	 */
//	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		response.getWriter().append("Served at: ").append(request.getContextPath());
//        processRequest(request, response);
//	}
//
//	/**
//	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
//	 */
//	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		doGet(request, response);
//        processRequest(request, response);
//	}
//	
//	   /**
//     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
//     * methods.
//     *
//     * @param request servlet request
//     * @param response servlet response
//     * @throws ServletException if a servlet-specific error occurs
//     * @throws IOException if an I/O error occurs
//     */
//    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        response.setContentType("text/html;charset=UTF-8");
//
//        //HttpSession session = request.getSession();
//        String startDate = request.getParameter("startDate");
//        String endDate = request.getParameter("endDate");
//        Semantics semantics = Semantics.valueOf(request.getParameter("semantics"));
//        int confidence = Integer.parseInt(request.getParameter("confidenceScore"));
//        ExplorationType explorationType = ExplorationType.valueOf(request.getParameter("explorationType"));
//        String[] entitiesStr = request.getParameterMap().get("entity[]");
//        String[] entityTypesStr = request.getParameterMap().get("entityType[]");
//        String[] entityPropertyNames = request.getParameterMap().get("entityPropertyName[]");
//        String[] entityPropertyValues = request.getParameterMap().get("entityPropertyValue[]");
//
//        ArrayList<String> entities = new ArrayList<>();
//        for (String ent : entitiesStr) {
//            if (!ent.trim().equals("")) {
//                entities.add(ent.trim());
//            }
//        }
//
//        ArrayList<String> entityTypes = new ArrayList<>();
//        for (String entType : entityTypesStr) {
//            if (!entType.trim().equals("")) {
//                entityTypes.add(entType.trim());
//            }
//        }
//
//        HashMap<String, String> entityChars = new HashMap<>();
//        for (int i = 0; i < entityPropertyNames.length; i++) {
//            if (!entityPropertyNames[i].trim().equals("")) {
//                entityChars.put(entityPropertyNames[i].trim(), entityPropertyValues[i].trim());
//            }
//        }
//
//        System.out.println("Start date: " + startDate);
//        System.out.println("End date: " + endDate);
//        System.out.println("Semantics: " + semantics);
//        System.out.println("Confidence: " + confidence);
//        System.out.println("Explore: " + explorationType);
//
//        System.out.println("Entities: " + entities.toString());
//        System.out.println("Entity types: " + entityTypes.toString());
//        System.out.println("Entity chars: " + entityChars.toString());
//        
//        String query = null;
//        switch (explorationType) {
//            case ENTITY_BASED:
//                QueryBuilder qb = new QueryBuilder(startDate, endDate, entities, semantics, confidence);
//                qb.createQuery();
//                query = qb.getQueryBuilder().toString();
//                break;
//            case ENTITY_TYPE_BASED:
//                break;
//            case ENTITY_PROPERTY_BASED:
//                break;      
//            default:
//                throw new AssertionError("Unknown ExplorationType " + explorationType);
//        }
//
//        try (PrintWriter out = response.getWriter()) {
//            /* TODO output your page here. You may use following sample code. */
//            out.println("<!DOCTYPE html>");
//            out.println("<html>");
//            out.println("<head>");
//            out.println("<title>Servlet Explore</title>");
//            out.println("</head>");
//            out.println("<body>");
//            //out.println("<h1>Servlet Explore at " + request.getContextPath() + "</h1>");
//            out.println("<h1>Query: </h1>");
//            out.print("<textarea rows=\"40\" cols=\"200\">");
//            out.print(query);
//            out.print("</textarea>");
//            out.println("</body>");
//            out.println("</html>");
//        }
//    }
//
//    /**
//     * Returns a short description of the servlet.
//     *
//     * @return a String containing servlet description
//     */
//    @Override
//    public String getServletInfo() {
//        return "Short description";
//    }// </editor-fold>
//
//}
