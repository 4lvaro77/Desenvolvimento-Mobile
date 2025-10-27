package com.example.hamburgueriaz;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private int quantidade = 0;
    private TextView quantidadeView;
    private EditText nomeCliente;
    private TextView resumoPedido;
    private TextView valorTotal;
    private CheckBox baconCheckBox;
    private CheckBox queijoCheckBox;
    private CheckBox onionRingsCheckBox;

    private final float PRECO_HAMBURGUER = 20.0f;
    private final float PRECO_BACON = 2.0f;
    private final float PRECO_QUEIJO = 2.0f;
    private final float PRECO_ONION_RINGS = 3.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar componentes
        initComponents();

        // Configurar listeners
        setupListeners();
    }

    private void initComponents() {
        quantidadeView = findViewById(R.id.quantidade);
        nomeCliente = findViewById(R.id.nome_cliente);
        resumoPedido = findViewById(R.id.resumo_pedido);
        valorTotal = findViewById(R.id.valor_total);
        baconCheckBox = findViewById(R.id.bacon);
        queijoCheckBox = findViewById(R.id.queijo);
        onionRingsCheckBox = findViewById(R.id.onion_rings);
    }

    private void setupListeners() {
        Button btnAdicionar = findViewById(R.id.adicionar);
        Button btnSubtrair = findViewById(R.id.subtrair);
        Button btnEnviar = findViewById(R.id.enviar_pedido);

        btnAdicionar.setOnClickListener(v -> adicionarQuantidade());
        btnSubtrair.setOnClickListener(v -> subtrairQuantidade());
        btnEnviar.setOnClickListener(v -> enviarPedido());

        // Listeners para os checkboxes
        CompoundButton.OnCheckedChangeListener checkboxListener =
                (buttonView, isChecked) -> atualizarResumoPedido();

        baconCheckBox.setOnCheckedChangeListener(checkboxListener);
        queijoCheckBox.setOnCheckedChangeListener(checkboxListener);
        onionRingsCheckBox.setOnCheckedChangeListener(checkboxListener);
    }

    private void adicionarQuantidade() {
        quantidade++;
        atualizarQuantidade();
        atualizarResumoPedido();
    }

    private void subtrairQuantidade() {
        if (quantidade > 0) {
            quantidade--;
            atualizarQuantidade();
            atualizarResumoPedido();
        }
    }

    private void atualizarQuantidade() {
        quantidadeView.setText(String.valueOf(quantidade));
    }

    private void atualizarResumoPedido() {
        StringBuilder resumo = new StringBuilder();

        String nome = nomeCliente.getText().toString();
        if (!nome.isEmpty()) {
            resumo.append("Cliente: ").append(nome).append("\n\n");
        }

        resumo.append("Quantidade: ").append(quantidade).append("\n");
        resumo.append("Adicionais:\n");

        if (baconCheckBox.isChecked()) {
            resumo.append("• Bacon\n");
        }
        if (queijoCheckBox.isChecked()) {
            resumo.append("• Queijo\n");
        }
        if (onionRingsCheckBox.isChecked()) {
            resumo.append("• Onion Rings\n");
        }

        if (!baconCheckBox.isChecked() && !queijoCheckBox.isChecked() && !onionRingsCheckBox.isChecked()) {
            resumo.append("Nenhum adicional selecionado\n");
        }

        float total = calcularTotal();
        resumo.append("\nTotal: R$ ").append(String.format("%.2f", total));

        resumoPedido.setText(resumo.toString());
        valorTotal.setText("VALOR TOTAL: R$ " + String.format("%.2f", total));
    }

    private float calcularTotal() {
        float totalAdicionais = 0;

        if (baconCheckBox.isChecked()) totalAdicionais += PRECO_BACON;
        if (queijoCheckBox.isChecked()) totalAdicionais += PRECO_QUEIJO;
        if (onionRingsCheckBox.isChecked()) totalAdicionais += PRECO_ONION_RINGS;

        return (PRECO_HAMBURGUER + totalAdicionais) * quantidade;
    }

    private void enviarPedido() {
        String nome = nomeCliente.getText().toString().trim();

        if (nome.isEmpty()) {
            Toast.makeText(this, "Por favor, informe seu nome", Toast.LENGTH_SHORT).show();
            return;
        }

        if (quantidade == 0) {
            Toast.makeText(this, "Por favor, selecione a quantidade", Toast.LENGTH_SHORT).show();
            return;
        }


        StringBuilder emailBody = new StringBuilder();
        emailBody.append("PEDIDO DA HAMBURGUERIAZ\n\n");
        emailBody.append("Cliente: ").append(nome).append("\n\n");
        emailBody.append("Quantidade: ").append(quantidade).append("\n");
        emailBody.append("Adicionais:\n");

        if (baconCheckBox.isChecked()) emailBody.append("• Bacon\n");
        if (queijoCheckBox.isChecked()) emailBody.append("• Queijo\n");
        if (onionRingsCheckBox.isChecked()) emailBody.append("• Onion Rings\n");

        if (!baconCheckBox.isChecked() && !queijoCheckBox.isChecked() && !onionRingsCheckBox.isChecked()) {
            emailBody.append("Nenhum adicional\n");
        }

        float total = calcularTotal();
        emailBody.append("\nTOTAL: R$ ").append(String.format("%.2f", total));

        enviarEmail(nome, emailBody.toString());
    }

    private void enviarEmail(String nome, String corpoEmail) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"pedidos@hamburgueriaz.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Pedido de " + nome);
        intent.putExtra(Intent.EXTRA_TEXT, corpoEmail);

        try {
            startActivity(Intent.createChooser(intent, "Enviar pedido por email"));
        } catch (Exception e) {
            Toast.makeText(this, "Nenhum app de email encontrado", Toast.LENGTH_SHORT).show();
        }
    }
}