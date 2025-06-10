package com.java6.todolist.manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.java6.todolist.Client;
import com.java6.todolist.format.MemberJson;
import com.java6.todolist.format.ResultData;
import com.java6.todolist.format.ScheduleJson;
import com.java6.todolist.util.*;

public class MenuManager {
	
	static int scanInput;
	Gson gson = new Gson();
	static JsonObject request;
	static ResultData resultData;
	
	// 첫번째화면(로그인||회원가입)
	public static void loginMenu(Client client,BufferedReader in, PrintWriter out, BufferedReader userInput) throws Exception {
		while (true) {
			System.out.println("========================");
			System.out.println("1. 회원가입");
			System.out.println("2. 로그인");
			System.out.println("0. 종료");
			System.out.println("========================");
			System.out.print("숫자를 입력하세요(0~2) : ");
			
			try {
				scanInput = Integer.parseInt(userInput.readLine());
			}catch (NumberFormatException e) {
				System.out.println("숫자만 입력하세요");
				continue;
			}
			
			try {
				switch (scanInput) {
		            case 1:
		                CommandUtil.cmdIn(client, "signup", userInput, out);
		                request = JsonParser.parseString(in.readLine()).getAsJsonObject();
		                resultData = JsonUtil.parseToDto(request, ResultData.class);
		                if(resultData.result) {
		                	System.out.println("회원가입에 성공하셨습니다(ID : " + resultData.message + ")");
		                }
		                else {
		                	ErrorUtil.handleError(resultData.message.toString());
		                }
		                break;
		            case 2:
		            	CommandUtil.cmdIn(client, "login", userInput, out);
		            	request = JsonParser.parseString(in.readLine()).getAsJsonObject();
		                resultData = JsonUtil.parseToDto(request, ResultData.class);
		            	if(resultData.result) {
		                	System.out.println("로그인에 성공하셨습니다(ID : " + resultData.message + ")");
		                	client.setId(resultData.message.toString());
		                	MenuManager.mainMenu(client, in, out, userInput);
		                }
		                else {
		                	ErrorUtil.handleError(resultData.message.toString());
		                }
			            break;
		            case 0:
		                return;
		            default:
		                System.out.println("잘못된 입력값입니다.");
				}
			} catch (IOException e) {
	            System.out.println("입출력 오류: " + e.getMessage());
	        }
		}
	}
		
	public static void mainMenu(Client client, BufferedReader in, PrintWriter out, BufferedReader userInput) throws Exception {
		while (true) {
			if(client.getId() == null) {
				return;
			}
			
			System.out.println("========================");
			System.out.println("1. 유저");
			System.out.println("2. 프로젝트");
			System.out.println("0. 로그아웃");
			System.out.println("========================");
			System.out.print("숫자를 입력하세요(0~2): ");
			
			try {
				scanInput = Integer.parseInt(userInput.readLine());
			} catch (NumberFormatException e) {
				System.out.println("숫자만 입력하세요.");
				continue;
			}

			try {
				switch (scanInput) {
					case 1:
						userPage(client, in, out, userInput);
						break;
					case 2:
						projectPage(client, in, out, userInput);
						break;
					case 0:
						client.setId(null);
						client.setProject(null);
						client.setScheduleIndex(0);
						return;
					default:
						System.out.println("잘못된 입력값입니다.");
				}
			} catch (IOException e) {
	            System.out.println("입출력 오류: " + e.getMessage());
	        }
			
		}
	}

	public static void userPage(Client client, BufferedReader in, PrintWriter out, BufferedReader userInput) throws Exception {
	    while (true) {
	    	System.out.println("========================");
	        System.out.println("1. 유저 정보");
	        System.out.println("2. 이름 변경");
	        System.out.println("3. 비밀번호 변경");
	        System.out.println("4. 회원탈퇴");
	        System.out.println("0. 돌아가기");
	        System.out.println("========================");
	        System.out.print("숫자를 입력하세요(0~4): ");
	        
	        try {
	            scanInput = Integer.parseInt(userInput.readLine());
	        } catch (NumberFormatException e) {
	            System.out.println("숫자만 입력하세요");
	            continue;
	        }

	       try {
	    	   switch (scanInput) {
	               case 1:
	                   CommandUtil.cmdIn(client, "fetchUser", userInput, out);
	                   request = JsonParser.parseString(in.readLine()).getAsJsonObject();
		               resultData = JsonUtil.parseToDto(request, ResultData.class);
	                   if (resultData.result) {
	                	   System.out.println("===유저 정보===");
	                	   List<String> profile = (List<String>) resultData.message;
	                       System.out.println("ID: " + profile.get(0));
	                       System.out.println("이름: " + profile.get(1));
	                   } else {
	                       ErrorUtil.handleError(resultData.message.toString());
	                   }
	                   break;
	
	               case 2:
	                   CommandUtil.cmdIn(client, "editUserName", userInput, out);
	                   request = JsonParser.parseString(in.readLine()).getAsJsonObject();
		               resultData = JsonUtil.parseToDto(request, ResultData.class);
	                   if (resultData.result) {
	                	   System.out.println("이름 수정에 성공하셨습니다(NAME : " + resultData.message + ")");
	                   } else {
	                       ErrorUtil.handleError(resultData.message.toString());
	                   }
	                   break;
	
	               case 3:
	                   CommandUtil.cmdIn(client, "editUserPassword", userInput, out);
	                   request = JsonParser.parseString(in.readLine()).getAsJsonObject();
		               resultData = JsonUtil.parseToDto(request, ResultData.class);
	                   if (resultData.result) {
	                   	System.out.println("비밀번호 수정에 성공하셨습니다.");
	                   } else {
	                       ErrorUtil.handleError(resultData.message.toString());
	                   }
	                   break;
	
	               case 4:
	               	   CommandUtil.cmdIn(client, "deleteUser", userInput, out);
	                   request = JsonParser.parseString(in.readLine()).getAsJsonObject();
		               resultData = JsonUtil.parseToDto(request, ResultData.class);
	                   if (resultData.result) {
	                	   System.out.println("회원탈퇴에 성공하셨습니다(ID : " + resultData.message + ")");
	                	   client.setId(null);
	                	   client.setProject(null);
	                	   client.setScheduleIndex(0);
		                   return;
	                   } else {
	                       ErrorUtil.handleError(resultData.message.toString());
	                   }
	                   break;
	                   
	               case 0:
		               	return;
	               default:
	                   System.out.println("잘못된 입력값입니다.");
	           }
	       } catch (IOException e) {
	            System.out.println("입출력 오류: " + e.getMessage());
	       }
	    }
	}
	
	// 프로젝트메뉴
	public static void projectPage(Client client, BufferedReader in, PrintWriter out, BufferedReader userInput) throws Exception {
		while (true) {
			System.out.println("========================");
			System.out.println("1. 프로젝트 생성");
			System.out.println("2. 프로젝트 삭제");
			System.out.println("3. 프로젝트 조회");
			System.out.println("4. 프로젝트 관리");
			System.out.println("0. 돌아가기");
			System.out.println("========================");
			System.out.print("숫자를 입력하세요(0~4): ");
			
			try {
				scanInput = Integer.parseInt(userInput.readLine());
			}catch (NumberFormatException e) {
				System.out.println("숫자만 입력하세요");
				continue;
			}
			
			try {
	    	   switch (scanInput) {
	               case 1:
	                   CommandUtil.cmdIn(client, "createProject", userInput, out);
	                   request = JsonParser.parseString(in.readLine()).getAsJsonObject();
		               resultData = JsonUtil.parseToDto(request, ResultData.class);
	                   if (resultData.result) {
	                       System.out.println("프로젝트 생성에 성공하였습니다(PROJECT :" + resultData.message + ")");
	                   } else {
	                       ErrorUtil.handleError(resultData.message.toString());
	                   }
	                   break;
	
	               case 2:
	                   CommandUtil.cmdIn(client, "deleteProject", userInput, out);
	                   request = JsonParser.parseString(in.readLine()).getAsJsonObject();
		               resultData = JsonUtil.parseToDto(request, ResultData.class);
	                   if (resultData.result) {
	                	   System.out.println("프로젝트 삭제에 성공하였습니다(PROJECT :" + resultData.message + ")");
	                   } else {
	                       ErrorUtil.handleError(resultData.message.toString());
	                   }
	                   break;
	
	               case 3:
	                   CommandUtil.cmdIn(client, "fetchUserProjects", userInput, out);
	                   request = JsonParser.parseString(in.readLine()).getAsJsonObject();
		               resultData = JsonUtil.parseToDto(request, ResultData.class);
	                   if (resultData.result) {
	                	   int i = 1;
	                	   System.out.println("=== 프로젝트 목록 ===");
	                	   List<String> projectList = (List<String>) resultData.message;
	                       for(String p : projectList) {
	                    	   System.out.println(i++ + ". " + p);
	                       }
	                   } else {
	                       ErrorUtil.handleError(resultData.message.toString());
	                   }
	                   break;
	
	               case 4:
	               	   CommandUtil.cmdIn(client, "selectProject", userInput, out);
	                   request = JsonParser.parseString(in.readLine()).getAsJsonObject();
		               resultData = JsonUtil.parseToDto(request, ResultData.class);
	                   if (resultData.result) {
	                	   System.out.println("선택한 프로젝트(PROJECT : " + resultData.message + ")");
	                	   client.setProject(resultData.message.toString());
	                	   MenuManager.editProjectPage(client, in, out, userInput);
	                	   return;
	                   } else {
	                       ErrorUtil.handleError(resultData.message.toString());
	                   }
	                   break;
	                   
	               case 0:
	            	   return;
	               	
	               default:
	                   System.out.println("잘못된 입력값입니다.");
	    	  }
	        } catch (IOException e) {
	           System.out.println("입출력 오류: " + e.getMessage());
	        }
		}
	}
	
	// 선택된 프로젝트 수정페이지
	public static void editProjectPage(Client client, BufferedReader in, PrintWriter out, BufferedReader userInput) throws Exception {
		while (true) {
			System.out.println("========================");
			System.out.println("1. 멤버");
			System.out.println("2. 스케줄");
			System.out.println("3. 프로젝트");
			System.out.println("0. 돌아가기");
			System.out.println("========================");
			System.out.print("숫자를 입력하세요(0~3): ");
			
			try {
				scanInput = Integer.parseInt(userInput.readLine());
			} catch (NumberFormatException e) {
				System.out.println("숫자만 입력하세요");
				continue;
			}
			
			try {
				switch (scanInput) {
					case 1:
						MenuManager.memberPage(client, in, out, userInput);
						break;
					case 2:
						MenuManager.schedulePage(client, in, out, userInput);
						break;
					case 3:
						MenuManager.editProjectValuePage(client, in, out, userInput);
						break;
					case 0:
						return; // 상위 메뉴로 복귀
					default:
						System.out.println("잘못된 입력값입니다.");
				}
			} catch (IOException e) {
				System.out.println("입출력 오류: " + e.getMessage());
			}
		}
	}

	// 프로젝트 수정 페이지(프로젝트 || 기한 ) + 선택한 프로젝트 userInput으로 보내서 바로 처리
	public static void editProjectValuePage(Client client, BufferedReader in, PrintWriter out, BufferedReader userInput) throws Exception {
		while (true) {
			System.out.println("========================");
			System.out.println("1. 프로젝트명 수정");
			System.out.println("2. 프로젝트기한 수정");
			System.out.println("0. 돌아가기");
			System.out.println("========================");
			System.out.print("숫자를 입력하세요(0~2): ");
			
			try {
				scanInput = Integer.parseInt(userInput.readLine());
			} catch (NumberFormatException e) {
				System.out.println("숫자만 입력하세요");
				continue;
			}

			try {
				switch (scanInput) {
					case 1:
						// 프로젝트명 수정
						CommandUtil.cmdIn(client, "editProjectTitle", userInput, out);
						request = JsonParser.parseString(in.readLine()).getAsJsonObject();
						resultData = JsonUtil.parseToDto(request, ResultData.class);
						if (resultData.result) {
							System.out.println("프로젝트명이 성공적으로 수정되었습니다. (NEW TITLE: " + resultData.message + ")");
							client.setProject(resultData.message.toString());
						} else {
							ErrorUtil.handleError(resultData.message.toString());
						}
						break;

					case 2:
						// 프로젝트 마감기한 수정
						CommandUtil.cmdIn(client, "editProjectDeadline", userInput, out);
						request = JsonParser.parseString(in.readLine()).getAsJsonObject();
						resultData = JsonUtil.parseToDto(request, ResultData.class);
						if (resultData.result) {
							System.out.println("마감기한이 성공적으로 수정되었습니다. (NEW DEADLINE: " + resultData.message + ")");
						} else {
							ErrorUtil.handleError(resultData.message.toString());
						}
						break;

					case 0:
						// 이전 메뉴로 돌아감
						return;

					default:
						System.out.println("잘못된 입력값입니다.");
				}
			} catch (IOException e) {
				System.out.println("입출력 오류: " + e.getMessage());
			}
		}
	}

	// 멤버 관련 페이지
	public static void memberPage(Client client, BufferedReader in, PrintWriter out, BufferedReader userInput) throws Exception {
		while (true) {
			System.out.println("========================");
			System.out.println("1. 멤버 추가");
			System.out.println("2. 멤버 삭제");
			System.out.println("3. 멤버 수정");
			System.out.println("4. 멤버 조회");
			System.out.println("0. 돌아가기");
			System.out.println("========================");
			System.out.print("숫자를 입력하세요(0~4): ");

			try {
				scanInput = Integer.parseInt(userInput.readLine());
			} catch (NumberFormatException e) {
				System.out.println("숫자만 입력하세요");
				continue;
			}

			try {
				switch (scanInput) {
					case 1: // 멤버 추가
						CommandUtil.cmdIn(client, "addMember", userInput, out);
						request = JsonParser.parseString(in.readLine()).getAsJsonObject();
						resultData = JsonUtil.parseToDto(request, ResultData.class);
						if (resultData.result) {
							System.out.println("멤버 추가에 성공하였습니다 (ID: " + resultData.message + ")");
						} else {
							ErrorUtil.handleError(resultData.message.toString());
						}
						break;

					case 2: // 멤버 삭제
						CommandUtil.cmdIn(client, "deleteMember", userInput, out);
						request = JsonParser.parseString(in.readLine()).getAsJsonObject();
						resultData = JsonUtil.parseToDto(request, ResultData.class);
						if (resultData.result) {
							System.out.println("멤버 삭제에 성공하였습니다 (ID: " + resultData.message + ")");
						} else {
							ErrorUtil.handleError(resultData.message.toString());
						}
						break;

					case 3: // 멤버 수정 (역할/권한 등은 서브메뉴에서 처리)
						MenuManager.editMemberPage(client, in, out, userInput);
						break;

					case 4: // 멤버 조회
						CommandUtil.cmdIn(client, "fetchMember", userInput, out);
						request = JsonParser.parseString(in.readLine()).getAsJsonObject();
						resultData = JsonUtil.parseToDto(request, ResultData.class);
						if (resultData.result) {
							Gson gson = new Gson();
							String jsonList = gson.toJson(resultData.message);
							Type listType = new TypeToken<List<MemberJson>>(){}.getType();
							List<MemberJson> memberList = gson.fromJson(jsonList, listType);
							
							System.out.println("=== 멤버 목록(ID|역할|권한) ===");
							int i = 1;
							for (MemberJson member : memberList) {
								System.out.println((i++) + ". " + member.getId() + " | " + member.getRole() + " | "  + member.getPermission());
							}
						} else {
							ErrorUtil.handleError(resultData.message.toString());
						}
						break;

					case 0: // 돌아가기
						return;

					default:
						System.out.println("잘못된 입력값입니다.");
				}
			} catch (IOException e) {
				System.out.println("입출력 오류: " + e.getMessage());
			}
		}
	}

	
	// 멤버 수정 페이지
	public static void editMemberPage(Client client, BufferedReader in, PrintWriter out, BufferedReader userInput) throws Exception {
		while (true) {
			System.out.println("========================");
			System.out.println("1. 멤버 역할 수정");
			System.out.println("2. 멤버 권한 수정");
			System.out.println("0. 돌아가기");
			System.out.println("========================");
			System.out.print("숫자를 입력하세요(0~2): ");

			try {
				scanInput = Integer.parseInt(userInput.readLine());
			} catch (NumberFormatException e) {
				System.out.println("숫자만 입력하세요");
				continue;
			}

			try {
				switch (scanInput) {
					case 1: // 멤버 역할 수정
						CommandUtil.cmdIn(client, "editMemberRole", userInput, out);
						request = JsonParser.parseString(in.readLine()).getAsJsonObject();
						resultData = JsonUtil.parseToDto(request, ResultData.class);
						if (resultData.result) {
							System.out.println("멤버 역할 수정에 성공하였습니다 (ID: " + resultData.message + ")");
						} else {
							ErrorUtil.handleError(resultData.message.toString());
						}
						break;

					case 2: // 멤버 권한 수정
						CommandUtil.cmdIn(client, "editMemberPermission", userInput, out);
						request = JsonParser.parseString(in.readLine()).getAsJsonObject();
						resultData = JsonUtil.parseToDto(request, ResultData.class);
						if (resultData.result) {
							System.out.println("멤버 권한 수정에 성공하였습니다 (ID: " + resultData.message + ")");
						} else {
							ErrorUtil.handleError(resultData.message.toString());
						}
						break;

					case 0: // 돌아가기
						return;

					default:
						System.out.println("잘못된 입력값입니다.");
				}
			} catch (IOException e) {
				System.out.println("입출력 오류: " + e.getMessage());
			}
		}
	}

	
	// 스케줄 페이지
	public static void schedulePage(Client client, BufferedReader in, PrintWriter out, BufferedReader userInput) throws Exception {
		while (true) {
			System.out.println("========================");
			System.out.println("1. 스케줄 추가");
			System.out.println("2. 스케줄 조회");
			System.out.println("0. 돌아가기");
			System.out.println("========================");
			System.out.print("숫자를 입력하세요(0~3): ");

			try {
				scanInput = Integer.parseInt(userInput.readLine());
			} catch (NumberFormatException e) {
				System.out.println("숫자만 입력하세요");
				continue;
			}

			try {
				switch (scanInput) {
					case 1: // 스케줄 추가
						CommandUtil.cmdIn(client, "addSchedule", userInput, out);
						request = JsonParser.parseString(in.readLine()).getAsJsonObject();
						resultData = JsonUtil.parseToDto(request, ResultData.class);
						if (resultData.result) {
							System.out.println("스케줄이 성공적으로 추가되었습니다. (TITLE: " + resultData.message + ")");
						} else {
							ErrorUtil.handleError(resultData.message.toString());
						}
						break;

					case 2: // 스케줄 조회
						CommandUtil.cmdIn(client, "fetchSchedule", userInput, out);
						request = JsonParser.parseString(in.readLine()).getAsJsonObject();
						resultData = JsonUtil.parseToDto(request, ResultData.class);

						if (resultData.result) {
							Gson gson = new Gson();
							String jsonList = gson.toJson(resultData.message);
							Type listType = new TypeToken<List<ScheduleJson>>() {}.getType();
							List<ScheduleJson> scheduleList = gson.fromJson(jsonList, listType);

							System.out.println("=== 스케줄 목록(제목|시작기한|마감기한|할당인원) ===");
							int i = 1;
							for (ScheduleJson s : scheduleList) {
								System.out.println((i++) + ". " + s.getTitle() + " | " + s.getStart() + " | " +s.getEnd());
								System.out.print("   할당인원 : ");
								for(String m : s.getAssigned()) {
									System.out.print(m + " ");
								}
								System.out.println();
							}

							// 조회 후 수정 메뉴로 진입 여부 묻기
							System.out.print("수정할 스케줄 번호가 있으면 입력, 없으면 Enter: ");
							String input = userInput.readLine();
							
							
							if (!input.isBlank()) {
								try {
									int idx = Integer.parseInt(input);
									if (idx < 1 || idx > scheduleList.size()) {
										System.out.println("❗ 유효하지 않은 번호입니다. 메뉴로 돌아갑니다.");
										break;
									}
									client.setScheduleIndex(idx);
									MenuManager.editSchedulePage(client, in, out, userInput);
								} catch (NumberFormatException e) {
									System.out.println("숫자가 아닌 입력입니다. 메뉴로 돌아갑니다.");
								}
							}
						} else {
							ErrorUtil.handleError(resultData.message.toString());
						}
						break;


					case 0: // 돌아가기
						return;

					default:
						System.out.println("잘못된 입력값입니다.");
				}
			} catch (IOException e) {
				System.out.println("입출력 오류: " + e.getMessage());
			}
		}
	}

	
	// 스케줄 수정 페이지
	public static void editSchedulePage(Client client, BufferedReader in, PrintWriter out, BufferedReader userInput) throws Exception {
		while (true) {
			System.out.println("========================");
			System.out.println("1. 스케줄명 수정");
			System.out.println("2. 스케줄 시작일 수정");
			System.out.println("3. 스케줄 마감일 수정");
			System.out.println("4. 스케줄 할당 멤버 수정");
			System.out.println("5. 스케줄 삭제");
			System.out.println("0. 돌아가기");
			System.out.println("========================");
			System.out.print("숫자를 입력하세요(0~5): ");

			try {
				scanInput = Integer.parseInt(userInput.readLine());
			} catch (NumberFormatException e) {
				System.out.println("숫자만 입력하세요");
				continue;
			}

			try {
				switch (scanInput) {
					case 1: // 스케줄 제목 수정
						CommandUtil.cmdIn(client, "editScheduleTitle", userInput, out);
						request = JsonParser.parseString(in.readLine()).getAsJsonObject();
						resultData = JsonUtil.parseToDto(request, ResultData.class);
						if (resultData.result) {
							System.out.println("스케줄 제목 수정 성공 (NEW TITLE: " + resultData.message + ")");
						} else {
							ErrorUtil.handleError(resultData.message.toString());
						}
						break;

					case 2: // 시작일 수정
						CommandUtil.cmdIn(client, "editScheduleStartDate", userInput, out);
						request = JsonParser.parseString(in.readLine()).getAsJsonObject();
						resultData = JsonUtil.parseToDto(request, ResultData.class);
						if (resultData.result) {
							System.out.println("시작일 수정 성공 (NEW START DATE: " + resultData.message + ")");
						} else {
							ErrorUtil.handleError(resultData.message.toString());
						}
						break;

					case 3: // 마감일 수정
						CommandUtil.cmdIn(client, "editScheduleDeadline", userInput, out);
						request = JsonParser.parseString(in.readLine()).getAsJsonObject();
						resultData = JsonUtil.parseToDto(request, ResultData.class);
						if (resultData.result) {
							System.out.println("마감일 수정 성공 (NEW DEADLINE: " + resultData.message + ")");
						} else {
							ErrorUtil.handleError(resultData.message.toString());
						}
						break;

					case 4: // 할당 멤버 수정 페이지로 이동
						MenuManager.editAssignedMemberPage(client, in, out, userInput);
						break;

					case 5: // 스케줄 삭제
						CommandUtil.cmdIn(client, "deleteSchedule", userInput, out);
						request = JsonParser.parseString(in.readLine()).getAsJsonObject();
						resultData = JsonUtil.parseToDto(request, ResultData.class);
						if (resultData.result) {
							System.out.println("스케줄이 삭제되었습니다.");
							return;
						} else {
							ErrorUtil.handleError(resultData.message.toString());
						}
						break;
						
					case 0: // 돌아가기
						return;

					default:
						System.out.println("잘못된 입력값입니다.");
				}
			} catch (IOException e) {
				System.out.println("입출력 오류: " + e.getMessage());
			}
		}
	}

	
	// 프로젝트 스케줄 할당 멤버 관리 페이지
	public static void editAssignedMemberPage(Client client, BufferedReader in, PrintWriter out, BufferedReader userInput) throws Exception {
		while (true) {
			System.out.println("========================");
			System.out.println("1. 할당 멤버 추가");
			System.out.println("2. 할당 멤버 삭제");
			System.out.println("0. 돌아가기");
			System.out.println("========================");
			System.out.print("숫자를 입력하세요(0~2): ");

			try {
				scanInput = Integer.parseInt(userInput.readLine());
			} catch (NumberFormatException e) {
				System.out.println("숫자만 입력하세요");
				continue;
			}

			try {
				switch (scanInput) {
					case 1: // 할당 멤버 추가
						CommandUtil.cmdIn(client, "addScheduleAssigned", userInput, out);
						request = JsonParser.parseString(in.readLine()).getAsJsonObject();
						resultData = JsonUtil.parseToDto(request, ResultData.class);
						if (resultData.result) {
							System.out.println("할당 멤버 추가 성공 (ID: " + resultData.message + ")");
						} else {
							ErrorUtil.handleError(resultData.message.toString());
						}
						break;

					case 2: // 할당 멤버 삭제
						CommandUtil.cmdIn(client, "deleteScheduleAssigned", userInput, out);
						request = JsonParser.parseString(in.readLine()).getAsJsonObject();
						resultData = JsonUtil.parseToDto(request, ResultData.class);
						if (resultData.result) {
							System.out.println("할당 멤버 삭제 성공 (ID: " + resultData.message + ")");
						} else {
							ErrorUtil.handleError(resultData.message.toString());
						}
						break;

					case 0: // 돌아가기
						return;

					default:
						System.out.println("잘못된 입력값입니다.");
				}
			} catch (IOException e) {
				System.out.println("입출력 오류: " + e.getMessage());
			}
		}
	}

}
