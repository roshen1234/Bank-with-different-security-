import { Role } from "./role.model";

export class User{

  public id: number;
  public name: string;
  public mobileNumber: string;
  public email : string;
  public password: string;
  public role : string;
  public statusCd: string;
  public statusMsg : string;
  public authStatus : string;

  // newly added fields to match backend response
  public userName: string;
  public firstName: string;
  public lastName: string;
  public enabled: boolean;
  public date: string;
  public roles: Role[];

  constructor(id?: number,name?: string, mobileNumber?: string, email?: string,  password?: string,role?: string,
      statusCd?:string,statusMsg?:string, authStatus?:string,
      userName?: string, firstName?: string, lastName?: string, enabled?: boolean, date?: string, roles?: Role[]){
        this.id = id || 0;
        this.name = name || '';
        this.mobileNumber = mobileNumber || '';
        this.email = email || '';
        this.password = password || '';
        this.role = role || '';
        this.statusCd = statusCd || '';
        this.statusMsg = statusMsg || '';
        this.authStatus = authStatus || '';

        this.userName = userName || '';
        this.firstName = firstName || '';
        this.lastName = lastName || '';
        this.enabled = enabled || false;
        this.date = date || '';
        this.roles = roles || [];
  }

}