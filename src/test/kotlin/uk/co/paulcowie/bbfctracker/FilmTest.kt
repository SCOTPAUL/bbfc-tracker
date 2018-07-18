package uk.co.paulcowie.bbfctracker

import org.hamcrest.CoreMatchers.containsString
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@RunWith(SpringRunner::class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(outputDir = "build/snippets")
class FilmTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun shouldReturnAll() {
        mockMvc.perform(get("/films/all"))
                .andDo(print())
                .andExpect(status().isOk)
                .andExpect(content().string(containsString("PG")))
                .andDo(document("films"))
    }
}