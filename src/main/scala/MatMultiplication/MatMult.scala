package matmult

import chisel3._
import chisel3.iotesters.{PeekPokeTester, Driver}
import scala.collection.mutable.ArrayBuffer

class MatMult extends Module {
  val io = IO(new Bundle {
    val matA   = Input(Vec(15, UInt(32.W)))
    val matB   = Input(Vec(10, UInt(32.W)))
    val load  = Input(Bool())
    val matC = Output(Vec(6, UInt(32.W)))
    val valid = Output(Bool())
  })
  var sum = UInt(32.W)
  val matA = new ArrayBuffer[UInt]()
  val matB = new ArrayBuffer[UInt]()
  val matC = new ArrayBuffer[UInt]()
  for(i <- 0 until 15) {                
		matA += io.matA(i)
  }
  for(i <- 0 until 10) {                
		matB += io.matB(i)
  }
  for(i <- 0 until 6) {                
		matC += 0.asUInt(32.W)
  }
  when (io.load) {
	for(i <- 0 until 3) {
		for(j <- 0 until 2) {
			sum = 0.asUInt(32.W)
			for(k <- 0 until 5)
			{
				sum = sum + matA(i*5+k)*io.matB(k*2+j)

			}
			matC(i*2 + j) = sum
		}
	}
  io.valid := true.B
  } .otherwise {
	io.valid := false.B
	
  }
  
  
  
  val tbl = Vec(matC)
  io.matC := tbl
  
  
}

class MatMultTests(c: MatMult) extends PeekPokeTester(c) {

  for (i <- 0 until 15)
	poke(c.io.matA(i), i)
  for (i <- 0 until 10)
	poke(c.io.matB(i), i)

  poke(c.io.load, 1)
  step(1)
  expect(c.io.valid, 1)
  expect(c.io.matC(0), 60)
  expect(c.io.matC(1), 70)
  expect(c.io.matC(2), 160)
  expect(c.io.matC(3), 195)
  expect(c.io.matC(4), 260)
  expect(c.io.matC(5), 320)
}

object MatMult {
  def main(args: Array[String]): Unit = {
    if (!Driver(() => new MatMult())(c => new MatMultTests(c))) System.exit(1)
  }
}
