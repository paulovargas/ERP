export interface MenuResponse {
  id: number;
  name: string;
  address: string | null;
  position: number | null;
  root: number | null;
}

export interface MenuItem {
  id: number;
  nome: string;
  endereco: string | null;
  posicao: number | null;
  raiz: number | null;
}
