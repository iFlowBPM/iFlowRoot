CREATE TABLE smtp_error_codes (
    error_code VARCHAR(3) PRIMARY KEY,
   error_type VARCHAR(20) CHECK (error_type IN ('pending', 'success', 'temporary_failure', 'permanent_failure', 'hard_failure')),
    description TEXT
);

-- success / Informativo
INSERT INTO smtp_error_codes VALUES ('150', 'pending', 'Delivered to local SMTP.');

INSERT INTO smtp_error_codes VALUES ('211', 'success', 'Informação do servidor disponível.');
INSERT INTO smtp_error_codes VALUES ('214', 'success', 'Ajuda disponível no servidor.');
INSERT INTO smtp_error_codes VALUES ('220', 'success', 'O servidor de e-mail está pronto para receber ligações.');
INSERT INTO smtp_error_codes VALUES ('221', 'success', 'A ligação com o servidor de e-mail foi encerrada.');
INSERT INTO smtp_error_codes VALUES ('250', 'success', 'O e-mail foi enviado com sucesso.');
INSERT INTO smtp_error_codes VALUES ('251', 'success', 'O destinatário não é local, mas o e-mail foi redirecionado.');
INSERT INTO smtp_error_codes VALUES ('252', 'success', 'Não foi possível confirmar o destinatário, mas o e-mail será enviado.');
INSERT INTO smtp_error_codes VALUES ('354', 'success', 'Pode escrever a mensagem agora. Termine com um ponto numa nova linha.');

-- Falhas temporárias
INSERT INTO smtp_error_codes VALUES ('421', 'temporary_failure', 'O servidor de e-mail está temporariamente indisponível. Tente mais tarde.');
INSERT INTO smtp_error_codes VALUES ('450', 'temporary_failure', 'A caixa de correio do destinatário não está disponível de momento.');
INSERT INTO smtp_error_codes VALUES ('451', 'temporary_failure', 'Houve um problema temporário ao processar o e-mail. Tente novamente.');
INSERT INTO smtp_error_codes VALUES ('452', 'temporary_failure', 'O servidor não consegue processar o e-mail agora (possivelmente sem espaço). Tente mais tarde.');

-- Falhas permanentes
INSERT INTO smtp_error_codes VALUES ('500', 'permanent_failure', 'Erro de comunicação com o servidor. Contacte o suporte técnico.');
INSERT INTO smtp_error_codes VALUES ('501', 'permanent_failure', 'O endereço de e-mail está mal escrito ou inválido.');
INSERT INTO smtp_error_codes VALUES ('502', 'permanent_failure', 'O servidor não reconhece este comando. Pode haver um problema de configuração.');
INSERT INTO smtp_error_codes VALUES ('503', 'permanent_failure', 'O pedido foi feito fora de ordem. Tente novamente.');
INSERT INTO smtp_error_codes VALUES ('504', 'permanent_failure', 'O servidor não consegue processar o pedido tal como foi enviado.');
INSERT INTO smtp_error_codes VALUES ('550', 'permanent_failure', 'O endereço de e-mail do destinatário não existe ou foi recusado.');
INSERT INTO smtp_error_codes VALUES ('551', 'permanent_failure', 'O destinatário não está neste servidor. Verifique o endereço.');
INSERT INTO smtp_error_codes VALUES ('552', 'permanent_failure', 'A caixa de correio está cheia ou a mensagem é demasiado grande.');
INSERT INTO smtp_error_codes VALUES ('553', 'permanent_failure', 'O endereço de e-mail é inválido. Verifique se está correto.');
INSERT INTO smtp_error_codes VALUES ('554', 'permanent_failure', 'O e-mail foi rejeitado. Pode ter sido considerado spam.');

INSERT INTO smtp_error_codes VALUES ('901', 'hard_failure', 'Não foi possível comunicar com o servidor SMTP.');
INSERT INTO smtp_error_codes VALUES ('902', 'hard_failure', 'Delivery expired - tempo de vida na fila excedido.');
INSERT INTO smtp_error_codes VALUES ('999', 'hard_failure', 'Código Inexistente.');

INSERT INTO smtp_error_codes VALUES ('544', 'permanent_failure', 'Host ou nome de domínio não encontrado. Erro no serviço de nomes para o domínio especificado.');
INSERT INTO smtp_error_codes VALUES ('400', 'permanent_failure', 'Erro temporário indefinido. O servidor de destino pode estar inacessível.');
INSERT INTO smtp_error_codes VALUES ('441', 'permanent_failure', 'Erro temporário indefinido. O servidor de destino pode estar inacessível.');

CREATE TABLE email_request_log (
    request_id VARCHAR(36) NOT NULL,
    to_address VARCHAR(255) NOT NULL DEFAULT '',
    status VARCHAR(10) CHECK (status IN ('pending', 'sent', 'failed')),
    smtp_code VARCHAR(3),
    requested_at TIMESTAMP,
    processed_at TIMESTAMP,
    queue_id VARCHAR(20),
    locked int(1) DEFAULT 0,
    CONSTRAINT email_request_log_pkey PRIMARY KEY (request_id, to_address),
    CONSTRAINT fk_smtp_code FOREIGN KEY (smtp_code)
        REFERENCES smtp_error_codes(error_code)
);
