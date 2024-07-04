package br.com.franca.helpdesk.usecases;

import br.com.franca.helpdesk.domains.Cliente;
import br.com.franca.helpdesk.domains.Tecnico;
import br.com.franca.helpdesk.repositorys.ClienteRepository;
import br.com.franca.helpdesk.repositorys.TecnicoRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class RelatorioUseCase {

    private final Logger log = LoggerFactory.getLogger(RelatorioUseCase.class);

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private TecnicoRepository tecnicoRepository;

    public ByteArrayInputStream gerarRelatorioClientes() throws IOException {

        log.info("Gerando relatório de clientes");

        List<Cliente> clientes = clienteRepository.findAll();

        log.info("Clientes encontrados: {}", clientes.size());

        try (Workbook workbook = new XSSFWorkbook()) {

            log.info("Criando planilha");
            Sheet sheet = workbook.createSheet("Clientes");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(1).setCellValue("Nome");
            headerRow.createCell(2).setCellValue("Email");
            headerRow.createCell(3).setCellValue("DT_CADASTRO");
            headerRow.createCell(4).setCellValue("CPF");

            // Criar estilos de célula
            CreationHelper createHelper = workbook.getCreationHelper();
            CellStyle dateCellStyle = workbook.createCellStyle();
            dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy"));

            CellStyle cpfCellStyle = workbook.createCellStyle();
            cpfCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("000.000.000-00"));

            int rowNum = 1;
            for (Cliente cliente : clientes) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(cliente.getId());
                row.createCell(1).setCellValue(cliente.getNome());
                row.createCell(2).setCellValue(cliente.getEmail());

                Cell dataCadastroCell = row.createCell(3);
                dataCadastroCell.setCellValue(cliente.getDataCriacao());
                dataCadastroCell.setCellStyle(dateCellStyle);

                Cell cpfCell = row.createCell(4);
                cpfCell.setCellValue(cliente.getCpf());
                cpfCell.setCellStyle(cpfCellStyle);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            log.info("Relatório gerado com sucesso");

            return new ByteArrayInputStream(outputStream.toByteArray());
        }
    }

    public ByteArrayInputStream gerarRelatorioTecnicos() throws IOException {

        log.info("Gerando relatório de técnicos");
        List<Tecnico> listTecnicos = tecnicoRepository.findAll();
        log.info("Técnicos encontrados: {}", listTecnicos.size());

        try (Workbook workbook = new XSSFWorkbook()) {
            log.info("Criando planilha");
            Sheet sheet = workbook.createSheet("Tecnicos");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(1).setCellValue("Nome");
            headerRow.createCell(2).setCellValue("Email");
            headerRow.createCell(3).setCellValue("DT_CADASTRO");
            headerRow.createCell(4).setCellValue("CPF");

            // Criar estilos de célula
            CreationHelper createHelper = workbook.getCreationHelper();
            CellStyle dateCellStyle = workbook.createCellStyle();
            dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy"));

            CellStyle cpfCellStyle = workbook.createCellStyle();
            cpfCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("000.000.000-00"));

            int rowNum = 1;
            for (Tecnico tecnico : listTecnicos) {

                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(tecnico.getId());
                row.createCell(1).setCellValue(tecnico.getNome());
                row.createCell(2).setCellValue(tecnico.getEmail());

                Cell dataCadastroCell = row.createCell(3);
                dataCadastroCell.setCellValue(tecnico.getDataCriacao());
                dataCadastroCell.setCellStyle(dateCellStyle);

                Cell cpfCell = row.createCell(4);
                cpfCell.setCellValue(tecnico.getCpf());
                cpfCell.setCellStyle(cpfCellStyle);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            log.info("Relatório gerado com sucesso");
            return new ByteArrayInputStream(outputStream.toByteArray());
        }
    }
}