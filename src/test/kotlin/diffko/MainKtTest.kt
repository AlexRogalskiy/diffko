package diffko

import io.kotlintest.Spec
import io.kotlintest.extensions.TestListener
import io.kotlintest.extensions.allure.AllureExtension
import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec
import java.io.ByteArrayOutputStream
import java.io.PrintStream


class MainKtTest : BehaviorSpec() {
    private val outContent = ByteArrayOutputStream()
    private val errContent = ByteArrayOutputStream()
    private val originalOut = System.out
    private val originalErr = System.err
    override fun listeners() = listOf(this, AllureExtension)

    override fun beforeSpec(spec: Spec) {
        System.setOut(PrintStream(outContent))
        System.setErr(PrintStream(errContent))
    }

    init {
        given("two files") {
            val f1 = createTempFile().apply { writeText("abc") }.apply { deleteOnExit() }.absolutePath
            val f2 = createTempFile().apply { writeText("bcd") }.apply { deleteOnExit() }.absolutePath
            `when`("I call diffko with only file params") {
                main(arrayOf("-s", f1, "-r", f2))
                then("I get textual differences") {
                    outContent.toString("UTF-8") shouldBe "[[a]]bc<<d>>\n"
                    outContent.reset()
                }
            }
            `when`("I call diffko with  file params and --color") {
                main(arrayOf("-s", f1, "-r", f2, "--color"))
                then("I get textual differences") {
                    outContent.toString("UTF-8") shouldBe "'[31ma'[0mbc'[32md'[0m\n"
                    outContent.reset()
                }
            }
        }
    }

    override fun afterSpec(spec: Spec) {
        System.setOut(originalOut)
        System.setErr(originalErr)
    }
}