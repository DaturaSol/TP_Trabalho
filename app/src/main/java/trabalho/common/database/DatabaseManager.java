package trabalho.common.database;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

// TODO: Implement:
// 1. Proper parsing
// 2.


/**
 * Gerencia a conexão com o banco de dados SQLite para toda a aplicação.
 * Utiliza o padrão Singleton para garantir uma única instância da conexão.
 * Cria e inicializa o banco de dados a partir do schema.sql na primeira
 * execução.
 */
public class DatabaseManager {

    private static final String DB_FILENAME = "hr_management.sqlite";
    private static final String DB_URL = "jdbc:sqlite:" + DB_FILENAME;
    private static final String SCHEMA_FILE = "schema.sql";

    private static Connection connectionInstance = null;

    /**
     * Construtor privado para evitar a criação de instâncias diretas.
     */
    private DatabaseManager() {
    }

    /**
     * Retorna a instância única da conexão com o banco de dados.
     * Se a conexão ainda não foi estabelecida, este método a inicializa.
     *
     * @return A instância da conexão com o banco de dados.
     * @throws SQLException se ocorrer um erro ao conectar ao banco de dados.
     */
    public static Connection getConnection() throws SQLException {
        if (connectionInstance == null || connectionInstance.isClosed()) {
            initializeDatabase();
            connectionInstance = DriverManager.getConnection(DB_URL);
            System.out.println("Conexão com o SQLite estabelecida com sucesso.");
        }
        return connectionInstance;
    }

    /**
     * Fecha a conexão com o banco de dados, se estiver aberta.
     */
    public static void closeConnection() {
        try {
            if (connectionInstance != null && !connectionInstance.isClosed()) {
                connectionInstance.close();
                System.out.println("Conexão com o SQLite fechada.");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao fechar a conexão com o banco de dados: " + e.getMessage());
        }
    }

    /**
     * Verifica se o arquivo do banco de dados existe. Se não, cria e executa o
     * script do schema.
     */
    private static void initializeDatabase() {
        if (!Files.exists(Paths.get(DB_FILENAME))) {
            System.out.println(
                    "Banco de dados não encontrado. Criando e inicializando a partir de " + SCHEMA_FILE + "...");
            try (Connection conn = DriverManager.getConnection(DB_URL);
                    Statement stmt = conn.createStatement()) {

                // Lê todo o conteúdo do arquivo schema.sql
                String sqlScript = new String(Files.readAllBytes(Paths.get(SCHEMA_FILE)));

                // SQLite JDBC driver não suporta múltiplos comandos em uma única chamada
                // execute(),
                // então dividimos o script por ponto e vírgula.
                // Isso funciona para comandos simples, mas pode falhar com triggers ou blocos
                // complexos.
                // Para este projeto, é suficiente.
                String[] commands = sqlScript.split(";");
                for (String command : commands) {
                    if (!command.trim().isEmpty()) {
                        stmt.execute(command);
                    }
                }

                System.out.println("Banco de dados e schema inicializados com sucesso.");

            } catch (SQLException | IOException e) {
                System.err.println("Erro crítico ao inicializar o banco de dados: " + e.getMessage());
                // Em uma aplicação real, você poderia querer encerrar o programa aqui.
                e.printStackTrace();
            }
        }
    }
}