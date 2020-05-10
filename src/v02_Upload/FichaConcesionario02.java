package v02_Upload;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import modelo.Concesionario;
import modelo.controladores.ConcesionarioControlador;
import utils.RequestUtils;
import utils.SuperTipoServlet;


/**
 * Servlet implementation class PrimerServlet
 */
@WebServlet(description = "Primer Servlet", urlPatterns = { "/FichaConcesionario02" })
public class FichaConcesionario02 extends SuperTipoServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FichaConcesionario02() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Obtengo una HashMap con todos los par�metros del request, sea este del tipo que sea;
		HashMap<String, Object> hashMap = RequestUtils.requestToHashMap(request);
		
		// Para plasmar la informaci�n de un profesor determinado utilizaremos un par�metro, que debe llegar a este Servlet obligatoriamente
		// El par�metro se llama "idProfesor" y gracias a �l podremos obtener la informaci�n del profesor y mostrar sus datos en pantalla
		Concesionario con = null;
		// Obtengo el profesor a editar, en el caso de que el profesor exista se cargar�n sus datos, en el caso de que no exista quedar� a null
		try {
			int idCon = RequestUtils.getIntParameterFromHashMap(hashMap, "idConcesionario"); // Necesito obtener el id del profesor que se quiere editar. En caso de un alta
			// de profesor obtendr�amos el valor 0 como idProfesor
			if (idCon != 0) {
				con = (Concesionario ) ConcesionarioControlador.getControlador().find(idCon);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Inicializo unos valores correctos para la presentaci�n del profesor
		if (con == null) {
			con = new Concesionario();
		}
		if (con.getCif() == null) con.setCif("");
		if (con.getNombre() == null) con.setNombre("");
		if (con.getLocalidad() == null) con.setLocalidad("");
		
		// Ahora debo determinar cu�l es la acci�n que este p�gina deber�a llevar a cabo, en funci�n de los par�metros de entrada al Servlet.
		// Las acciones que se pueden querer llevar a cabo son tres:
		//    - "eliminar". S� que est� es la acci�n porque recibir� un un par�metro con el nombre "eliminar" en el request
		//    - "guardar". S� que est� es la acci�n elegida porque recibir� un par�metro en el request con el nombre "guardar"
		//    - Sin acci�n. En este caso simplemente se quiere editar la ficha
		
		// Variable con mensaje de informaci�n al usuario sobre alguna acci�n requerida
		String mensajeAlUsuario = "";
		
		// Primera acci�n posible: eliminar
		if (RequestUtils.getStringParameterFromHashMap(hashMap, "eliminar") != null) {
			// Intento eliminar el registro, si el borrado es correcto redirijo la petici�n hacia el listado de profesores
			try {
				ConcesionarioControlador.getControlador().remove(con);
				response.sendRedirect(request.getContextPath() + "ListadoConcesionarios02"); // Redirecci�n del response hacia el listado
			}
			catch (Exception ex) {
				mensajeAlUsuario = "Imposible eliminar. Es posible que existan restricciones.";
			}
		}
		
		// Segunda acci�n posible: guardar
		if (RequestUtils.getStringParameterFromHashMap(hashMap, "guardar") != null) {
			// Obtengo todos los datos del profesor y los almaceno en BBDD
			try {
				con.setCif(RequestUtils.getStringParameterFromHashMap(hashMap, "cif"));
				con.setNombre(RequestUtils.getStringParameterFromHashMap(hashMap, "nombre"));
				con.setLocalidad(RequestUtils.getStringParameterFromHashMap(hashMap, "localidad"));
				byte[] posibleImagen = RequestUtils.getByteArrayFromHashMap(hashMap, "ficheroImagen");
				if (posibleImagen != null && posibleImagen.length > 0) {
					con.setImagen(posibleImagen);
				}
				
				// Finalmente guardo el objeto de tipo profesor 
				ConcesionarioControlador.getControlador().save(con);
				mensajeAlUsuario = "Guardado correctamente";
			} catch (Exception e) {
				throw new ServletException(e);
			}
		}
		
		
		
		// Ahora muestro la pantalla de respuesta al usuario
		//A�ado la cabecera
		response.getWriter().append(this.getCabeceraHTML("Ficha de Concesionario"));
		
		//A�ado el cuerpo de la p�gina
		response.getWriter().append(
				"<body " +((mensajeAlUsuario != null && mensajeAlUsuario != "")? "onLoad=\"alert('" + mensajeAlUsuario + "');\"" : "")  + " >\r\n" + 
				"<h1>Ficha de Concesionario</h1>\r\n" + 
				"<a href=\"ListadoConcesionarios02\">Ir al listado de Concesionarios</a>" +
				//Form start
				"<form id=\"form1\" name=\"form1\" onsubmit=\"return validateForm()\" method=\"post\" action=\"FichaConcesionario02\" enctype=\"multipart/form-data\" >\r\n" + 
				//Imagen
				" <img src=\"utils/DownloadImagenConce?idConcesionario=" + con.getId() + "\" width='100px' height='100px'/>" +
				" <input type=\"hidden\" name=\"idConcesionario\" value=\"" + con.getId() + "\"\\>" +
				//Input de imagen
				"  <p>\r\n" + 
				"    <label for=\"ficheroImagen\">Imagen:</label>\r\n" + 
				"    <input name=\"ficheroImagen\" type=\"file\" id=\"ficheroImagen\" />\r\n" + 
				"  </p>\r\n" + 
				//Formulario para el CIF
				"  <p>\r\n" + 
				"    <label for=\"cif\">CIF:</label>\r\n" + 
				"    <input name=\"cif\" type=\"text\" id=\"cif\" value=\"" + con.getCif() + "\" />\r\n" + 
				"  </p>\r\n" +
				//Formulario para el Nombre
				"  <p>\r\n" + 
				"    <label for=\"nombre\">Nombre:</label>\r\n" + 
				"    <input name=\"nombre\" type=\"text\" id=\"nombre\"  value=\"" + con.getNombre() + "\" />\r\n" + 
				"  </p>\r\n" + 
				//Formulario para la localidad
				"  <p>\r\n" + 
				"    <label for=\"localidad\">Localidad:</label>\r\n" + 
				"    <input name=\"localidad\" type=\"text\" id=\"localidad\" value=\"" + con.getLocalidad() + "\" />\r\n" + 
				"  </p>\r\n" + 
				//Botones
				"  <p>\r\n" + 
				"    <input type=\"submit\" name=\"guardar\" value=\"Guardar\" />\r\n" + 
				"    <input type=\"submit\" name=\"eliminar\" value=\"Eliminar\" />\r\n" + 
				"  </p>\r\n" + 
				"</form>" + 
				"</body>\r\n" + 
				"</html>\r\n" + 
				"");

				response.getWriter().append(this.getPieHTML());
	}
}
