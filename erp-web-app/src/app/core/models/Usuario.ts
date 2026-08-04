export interface Usuario {
  idUsuarios: number;
  idGaragem: number;
  registro: number;
  login: string;
  senha: string;
  nome: string;
  nivel: number;
  monitor: string;
  dhRegistro: string;
  idUsuario: number;
  idAparelho: number;
  idANT: number;
  visivel: string;
}

export class Usuario implements Usuario {
  idUsuarios = 0;
  idGaragem = 0;
  registro = 0;
  login = '';
  senha = '';
  nome = '';
  nivel = 0;
  monitor = '';
  dhRegistro = '';
  idUsuario = 0;
  idAparelho = 0;
  idANT = 0;
  visivel = '';
}
