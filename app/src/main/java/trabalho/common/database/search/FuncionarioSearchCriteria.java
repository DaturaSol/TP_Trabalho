package trabalho.common.database.search;

/**
 * Holds optional criteria for searching Funcionarios.
 * Fields left null or empty will be ignored during the search.
 * 
 * @author Gabriel M.S.O.
 */
public record FuncionarioSearchCriteria(
    String nomePartial,   // Matches if name contains this string
    String cpfExact,      // Matches exact CPF
    String cargoExact,    // Matches exact job title
    String statusExact,   // Matches exact status (e.g., "Active")
    String departamentoExact // Matches exact department
) {}
