export type UserType = 'BUYER' | 'SELLER' | 'BUYER_SELLER' | 'ADMIN';
export type UserStatus = 'ACTIVE' | 'INACTIVE' | 'BLOCKED';

export interface ClientResponse {
  id: number;
  name: string;
  tradeName: string | null;
  email: string;
  document: string;
  phone: string | null;
  userType: UserType;
  status: UserStatus;
  sellerApproved: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface ClientCreateRequest {
  name: string;
  tradeName: string | null;
  email: string;
  password: string;
  document: string;
  phone: string | null;
  userType: UserType;
}

export interface ClientUpdateRequest {
  name: string;
  tradeName: string | null;
  email: string;
  password?: string;
  document: string;
  phone: string | null;
  userType: UserType;
  status: UserStatus;
  sellerApproved: boolean;
}
