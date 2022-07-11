package com.openai.board.dto;

import lombok.Data;

@Data 
//필드는 무조건 소문자로
//member 테이블, minfo 뷰 공용. 
public class MemberDto {
	private String m_id;
	private String m_pwd;
	private String m_name;
	private String m_birth;
	private String m_addr;
	private String m_phone;
	private int m_point;
	private String g_name;
	
}
